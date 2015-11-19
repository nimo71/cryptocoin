(ns cryptocoin.jawampa
  "https://github.com/Matthias247/jawampa"
  (:require [clojure.core.async :refer :all])
  (:import [ws.wamp.jawampa WampClientBuilder WampClient$ConnectedState]
           [ws.wamp.jawampa.transport.netty NettyWampClientConnectorProvider]
           [java.util.concurrent TimeUnit]))

(defn- client []
  (let [builder (doto (WampClientBuilder.)
                  (.withConnectorProvider (NettyWampClientConnectorProvider.))
                  ;(.withConnectionConfiguration connectionConfiguration)
                  (.withUri "wss://api.poloniex.com")
                  (.withRealm "realm1")
                  (.withInfiniteReconnects)
                  (.withReconnectInterval 1 TimeUnit/SECONDS))]
    (.build builder)))                                           ;;TODO: can throw exception

(defn- process-ticker [ticker-channel]
  (reify rx.functions.Action1
    (call [_ tick]

      (let [tick-args   (.arguments tick)
            tick-keys   [:currencyPair :last :lowestAsk :highestBid :percentChange
                         :baseVolume :quoteVolume :isFrozen :24hrHigh :24hrLow]
            tick-vals   (for [arg tick-args] (.asText arg))  ;; TODO: convert to correct type based on keys
            tick-event  (zipmap tick-keys tick-vals)]

        (put! ticker-channel {:poloniex :ticker
                              :value    tick-event})

        (comment println "args:" tick-event)))))

(defn- process-error [ticker-channel]
  (reify rx.functions.Action1
    (call [_ throwable]
      (>! ticker-channel {:error :ticker
                          :value {:throwable throwable}})
      (println "Throwable: " (.printStackTrace throwable)))))

(defn- process-default []
  (reify rx.functions.Action0
    (call [_]
      (println "Message sent."))))

(defn- process-connection [client ticker-channel]
  (reify rx.functions.Action1
    (call [_ state]
      (println "state:" state)
      (if (instance? WampClient$ConnectedState state)
        (-> client
            (.makeSubscription "ticker")
            (.subscribe
              (process-ticker ticker-channel)
              (process-error ticker-channel)
              (process-default)))))))

(defn- connect-and-subscribe [client ticker-channel]
  (-> client .statusChanged (.subscribe
                              (process-connection client ticker-channel)
                              (process-error ticker-channel)
                              (process-default))))
(defn start []
  (let [client         (client)
        ticker-channel (chan)]
    (connect-and-subscribe client ticker-channel)
    (.open client)
    ticker-channel))
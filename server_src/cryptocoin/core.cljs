(ns ^:figwheel-always cryptocoin.core
    (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

(def autobahn (nodejs/require "autobahn"))

(def wsuri "wss://api.poloniex.com")

(def conn
  (autobahn.Connection. (clj->js {:url wsuri
                                  :realm "realm1"})))
(def conn-opened 
  (fn [session]
    (let [market-event (fn [currs] (fn [args, kwargs]
                                     (println "market-event " currs " args=" (js->clj args))))

          ticker-event (fn [args, kwargs]
                         (let [tick-labels [:currencyPair :last :lowestAsk :highestBid :percentChange :baseVolume :quoteVolume :isFrozen :24hrHigh :24hrLow]
                               tick-event  (zipmap tick-labels args)]
                           (println "ticker-event" tick-event)))]

      (.subscribe session "BTC_USDT", (market-event "BTC_USDT"))
      (.subscribe session "BTC_ETH", (market-event "BTC_ETH"))
      (comment .subscribe session "BTC_USDT", market-event)
      (comment .subscribe session "ticker" ticker-event))))

(def conn-closed 
  (fn [] (println "Websocket connection closed")))

(def -main (fn [] 
             (println "Starting...")
             (set! (.-onopen conn) conn-opened)
             (set! (.-onclose conn) conn-closed)
             (.open conn)))

(set! *main-cli-fn* -main)

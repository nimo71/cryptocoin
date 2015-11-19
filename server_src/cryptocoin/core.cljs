(ns ^:figwheel-always cryptocoin.core
    (:require [cljs.nodejs :as nodejs]
              [cryptocoin.server :as server]))

(nodejs/enable-util-print!)

(def autobahn (nodejs/require "autobahn"))

(def wsuri "wss://api.poloniex.com")

(def conn
  (autobahn.Connection. (clj->js {:url   wsuri
                                  :realm "realm1"})))
(def conn-opened 
  (fn [session]
    (let [ticker-event (fn [args, kwargs]
                         (let [tick-labels ["currencyPair" "last" "lowestAsk" "highestBid" "percentChange"
                                            "baseVolume" "quoteVolume" "isFrozen" "24hrHigh" "24hrLow"]
                               tick-event (zipmap tick-labels args)]
                           (println "ticker-event" tick-event)
                           (>! tick-event ticker-channel)))]

      (comment server/start-server ticker-channel)
      (.subscribe session "ticker" ticker-event))))

(def conn-closed 
  (fn [] (println "Websocket connection closed")))

(def -main (fn [] 
             (println "Starting...")
             (set! (.-onopen conn) conn-opened)
             (set! (.-onclose conn) conn-closed)
             (.open conn)))

(set! *main-cli-fn* -main)

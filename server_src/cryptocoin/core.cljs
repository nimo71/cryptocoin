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
    (let [market-event (fn [args, kwargs]
                         (println "market-event" args))

          ticker-event (fn [args, kwargs]
                         (println "ticker-event" args))]

      (.subscribe session "BTX_XMR", market-event)
      (.subscribe session "ticker" ticker-event))))

(def conn-closed 
  (fn [] (println "Websocket connection closed")))

(def -main (fn [] 
             (println "Starting...")
             (set! (.-onopen conn) conn-opened)
             (set! (.-onclose conn) conn-closed)
             (.open conn)))

(set! *main-cli-fn* -main)

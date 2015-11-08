(ns cryptocoin.core
  (:require [clojure.core.async :refer :all]
            [cryptocoin.poloniex :as poloniex]
            [cryptocoin.market-history :as history]))

(defn -main [& args]
  (println "Starting...")

  (go-loop [returnTicker  (poloniex/returnTicker)]
    (history/update-markets returnTicker)
    (<! (timeout 1000))
    (recur (poloniex/returnTicker)))

  (println "Ctrl-C to finish")
  (loop [] (recur)))
(ns cryptocoin.core
  (:require [clojure.core.async :refer :all]
            [cryptocoin.poloniex :as poloniex]
            [cryptocoin.market-history :as history]))

(def every-second 1000)

(defn -main [& args]
  (println "Starting...")

  (let [ticker (poloniex/returnTicker-channel every-second)]
    (go-loop [market (<! ticker)]
      (history/update-markets market)
      (recur (<! ticker))))

  (println "Ctrl-C to finish")
  (loop [] (recur)))
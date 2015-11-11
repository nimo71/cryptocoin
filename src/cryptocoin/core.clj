(ns cryptocoin.core
  (:require [clojure.core.async :refer :all]
            [cryptocoin.poloniex :as poloniex]
            [cryptocoin.market-update :as market]))

(def every-second 1000)

(defn -main [& args]
  (println "Starting...")

  (let [markets-chan      (poloniex/returnTicker every-second)
        price-change-chan (market/market-update markets-chan)]

      (go-loop [price-change (:value (<! price-change-chan))]
        (let [pair (:pair price-change)
              from (:from price-change)
              to   (:to price-change)
              diff (- (read-string to) ((fnil read-string to) from))]

          (println pair "price changed, from:" from ", to:" to ", diff:" diff))

        (recur (:value (<! price-change-chan)))))

  (println "Ctrl-C to finish")
  (loop [] (recur)))

;; TODO: Hold last 1000 prices in history for each currency pair
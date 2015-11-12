(ns cryptocoin.core
  (:require [clojure.core.async :refer :all]
            [cryptocoin.poloniex :as poloniex]
            [cryptocoin.market-update :as market]
            [cryptocoin.price-history :as price-history]))

(def every-second 1000)

(defn -main [& args]
  (println "Starting...")

  (let [markets-chan (poloniex/returnTicker every-second)
        price-change-chan (market/market-update markets-chan)]

    (price-history/update price-change-chan)

    (go-loop [price-change (:value (<! price-change-chan))]

      (when-let [from (read-string (:from price-change))]
        (let [pair (:pair price-change)
              to   (read-string (:to price-change))
              diff (- to from)]

        (println pair "price changed, from:" from ", to:" to ", diff:" diff)))

      (recur (:value (<! price-change-chan)))))

  (println "Ctrl-C to finish")
  (loop [] (recur)))

(ns cryptocoin.core
  (:require [clojure.core.async :refer :all]
            [cryptocoin.poloniex :as poloniex]
            [cryptocoin.market-history :as history]))

(def every-second 1000)

(defn -main [& args]
  (println "Starting...")

  (let [chan-markets (chan)
        chan-price-change (chan)
        pub-returnTicker (poloniex/returnTicker (chan) every-second)
        pub-market-update (history/update-markets chan-markets)]

      (sub pub-returnTicker :returnTicker chan-markets)
      (sub pub-market-update :price-change chan-price-change)

      (go-loop [price-change (:value (<! chan-price-change))]
        (let [pair (:pair price-change)
              from (:from price-change)
              to (:to price-change)
              diff (- (read-string to) ((fnil read-string to) from))]

          (println pair "price changed, from:" from ", to:" to ", diff:" diff))

        (recur (:value (<! chan-price-change)))))

  (println "Ctrl-C to finish")
  (loop [] (recur)))
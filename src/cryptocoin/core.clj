(ns cryptocoin.core
  (:require [clojure.core.async :refer :all]
            [cryptocoin.poloniex :as poloniex]
            [cryptocoin.market-update :as market]
            [cryptocoin.price-history :as price-history]))

(def every-second 1000)

(defn print-price-change [price-change-chan]
  (go-loop [price-change (:value (<! price-change-chan))]

    (when-let [from (read-string (:from price-change))]
      (let [pair (:pair price-change)
            to (read-string (:to price-change))
            diff (- to from)]

        (println pair "price changed, from:" from ", to:" to ", diff:" diff)))

    (recur (:value (<! price-change-chan)))))

(defn print-price-history [price-history-chan]
  (go-loop [price-history (<! price-history-chan)]
    (println price-history)
    (recur (<! price-history-chan))))

(defn -main [& args]
  (println "Starting...")

  (let [markets-chan            (poloniex/returnTicker every-second)
        pub-markets             (pub markets-chan :poloniex)
        pub-error               (pub markets-chan :error)
        returnTicker-chan       (chan)
        returnTicker-error-chan (chan)
        market-update-chan      (market/market-update returnTicker-chan)
        pub-market-update       (pub market-update-chan :market-update)
        price-change-chan       (chan)
        price-history-chan      (price-history/update market-update-chan)]

    (sub pub-markets :returnTicker returnTicker-chan)
    (sub pub-error :returnTicker returnTicker-error-chan)
    (sub pub-market-update :price-change price-change-chan)

    (comment print-price-change price-change-chan)

    (print-price-history price-history-chan))

  (println "Ctrl-C to finish")
  (loop [] (recur)))

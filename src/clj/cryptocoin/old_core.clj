(ns cryptocoin.old-core
  (:require [clojure.core.async :refer :all]
            [cryptocoin.price-history :as price-history]
            [cryptocoin.jawampa :as websocket]))

(defn print-price-history [price-history-chan]
  (go-loop [price-history (<! price-history-chan)]
    (comment let [[[pair history]]  (vec price-history)
          time-diff         (- (:timestamp (first history)) (:timestamp (last history)))
          time-sec          (long (/ time-diff 1000))
          percent-diff      (reduce + 0 (map :percentChange history))]

      (println pair "price changed by" percent-diff "% in" time-sec "sec"))

    (println (str price-history))

    (recur (<! price-history-chan))))

(defn print-markets [ticker-chan]
  (go-loop [market (<! ticker-chan)]
    (println "market:" market)
    (recur (<! ticker-chan))))

(defn -main [& args]
  (println "Starting...")

  (let [markets-chan        (websocket/start)
        pub-markets         (pub markets-chan :poloniex)
        pub-error           (pub markets-chan :error)
        ticker-chan         (chan)
        ticker-error-chan   (chan)
        price-history-chan  (price-history/update ticker-chan)]

    (sub pub-markets :ticker ticker-chan)
    (sub pub-error :ticker ticker-error-chan)

    (print-price-history price-history-chan))

  (println "Ctrl-C to finish")
  (loop [] (recur)))

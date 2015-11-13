(ns cryptocoin.price-history
  (:require [clojure.core.async :refer :all]))

(def history (atom {}))

(defn- prepend-history [price-update history]
  (let [cons-vec (fnil cons [])]
    (cons-vec price-update history)))

(defn update [price-update-chan]
  (let [price-history-chan (chan)]

    (go-loop [price-update (<! price-update-chan)]

      (let [{:keys [pair from to timestamp]} (:value price-update)
            from-amt (read-string from)
            to-amt (read-string to)
            diff (- to-amt from-amt)
            percent (/ (* diff 100) from-amt)]

        (swap! history update-in [pair] #(prepend-history {:diff      diff
                                                           :timestamp timestamp
                                                           :percent   percent} %))
        (>! price-history-chan {pair (pair @history)}))

      (recur (<! price-update-chan)))

    price-history-chan))

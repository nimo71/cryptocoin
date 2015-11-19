(ns cryptocoin.price-history
  (:require [clojure.core.async :refer :all]))

(def history (atom {}))

(defn- prepend-history [price-update history]
  (let [cons-vec (fnil cons [])]
    (cons-vec price-update history)))

(defn update [price-update-chan]
  (let [price-history-chan (chan)]

    (go-loop [price-update (<! price-update-chan)]

      (let [{:keys [currencyPair percentChange last]} (:value price-update)
            amount          (read-string last)
            percent         (read-string percentChange)
            history-record  {:price          amount
                             :percentChange  percent
                             :timestamp      (System/currentTimeMillis)}]

        (swap! history update-in [currencyPair] #(prepend-history history-record %))
        (>! price-history-chan {currencyPair (get @history currencyPair)}))

      (recur (<! price-update-chan)))

    price-history-chan))

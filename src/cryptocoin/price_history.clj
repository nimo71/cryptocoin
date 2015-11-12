(ns cryptocoin.price-history
  (:require [clojure.core.async :refer :all]))

(def history (atom {}))

(defn- prepend-history [price-update history]
  (let [cons-vec (fnil cons [])]
    (cons-vec price-update history)))

(defn update [price-update-chan]
  (go-loop [{:keys [pair from to timestamp]} (:value (<! price-update-chan))]

    (let [diff (- (read-string to) (read-string from))]
      (swap! history update-in [pair] #(prepend-history {:diff diff :timestamp timestamp} %)))

    (recur (:value (<! price-update-chan)))))
(ns cryptocoin.market-update
  "Notify of any updates to prices in the market"
  (:require [clojure.core.async :refer :all]))

(def markets (atom {}))

(defrecord PriceChange [pair from to timestamp])

(defn price-changed? [currency-pair market-value]
  (let [last-price (-> @markets currency-pair :last)
        new-price  (:last market-value)]
    (not= last-price new-price)))

(defn market-update [chan-markets]
  (let [market-update-chan (chan)]

    (go-loop [new-markets (<! chan-markets)]
      (doseq [[currency-pair market-value] (:value new-markets)]
        (when (price-changed? currency-pair market-value)
          (when-let [from (-> @markets currency-pair :last)]

            (>! market-update-chan {:market-update :price-change
                                    :value         (map->PriceChange {:pair      currency-pair
                                                                      :from      from
                                                                      :to        (:last market-value)
                                                                      :timestamp (System/currentTimeMillis)})}))

          (swap! markets assoc-in [currency-pair] market-value)))

      (recur (<! chan-markets)))

    market-update-chan))
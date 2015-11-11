(ns cryptocoin.market-update
  "Notify of any updates to prices in the market"
  (:require [clojure.core.async :refer :all]))

(def markets (atom {}))

(defn price-changed? [currency-pair market-value]
  (let [last-price (-> @markets currency-pair :last)
        new-price  (:last market-value)]
    (not= last-price new-price)))

(defn market-update [chan-markets]
  (let [market-update-chan (chan)]

    (go-loop [new-markets (<! chan-markets)]
      (doseq [[currency-pair market-value] (:value new-markets)]
        (when (price-changed? currency-pair market-value)

          (>! market-update-chan {:market-update :price-change
                                  :value         {:pair      currency-pair
                                                  :from      (-> @markets currency-pair :last)
                                                  :to        (:last market-value)
                                                  :timestamp (System/currentTimeMillis)}})

          (swap! markets assoc-in [currency-pair] market-value)))

      (recur (<! chan-markets)))

    market-update-chan))
(ns cryptocoin.market-history

  "Manage the history of the market for analysis.
  - The market-history atom defines a history of prices for each currency pair in any update provided by update-markets
  - market-history holds a vector of timestamped prices for each currency pair with the most recent price at the head
    e.g. {...
          :BTC_XMR [{:timestamp 123123123, :price 0.001312},
                   {:timestamp 112121212, :price 0.001423},
                   ...]
          ..."

  (:require [clojure.core.async :refer :all]))

(def market-history (atom {}))

(defn last-price-changed? [currency-pair market-value]
  (let [history-price (-> @market-history currency-pair first :price)
        price         (:last market-value)]
    (not= history-price price)))

(defn prepend-history [current-market-value history]
  (let [cons-vec (fnil cons [])
        change   {:timestamp (System/currentTimeMillis)
                  :price     (:last current-market-value)}]
    (cons-vec change history)))

(defn update-markets [chan-markets]

  (go-loop [markets (<! chan-markets)]
    (doseq [[currency-pair market-value] (:markets markets)]
      (when (last-price-changed? currency-pair market-value)
        (let [from (-> @market-history currency-pair first :price)
              to   (:last market-value)]

          (>! chan-markets {:market-update :price-change
                            :value         {:pair currency-pair
                                            :from from
                                            :to   to}})

          (swap! market-history update-in [currency-pair] #(prepend-history market-value %)))))

    (recur (<! chan-markets)))

  (pub chan-markets :market-update))


;; TODO: Don't hold history here - subscribers can maintain their own history
;; TODO: Hold last 1000 prices in history for each currency pair
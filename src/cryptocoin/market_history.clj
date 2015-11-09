(ns cryptocoin.market-history
  "Manage the history of the market for analysis.
  - The market-history atom defines a history of prices for each currency pair in any update provided by update-markets
  - market-history holds a vector of timestamped prices for each currency pair with the most recent price at the head
    e.g. {...
          :BTC_XMR [{:timestamp 123123123, :price 0.001312},
                   {:timestamp 112121212, :price 0.001423},
                   ...]
          ...")

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

(defn update-markets [current-markets]
  (doseq [[currency-pair current-market-value] current-markets]
    (when (last-price-changed? currency-pair current-market-value)
      (let [from  (-> @market-history currency-pair first :price)
            to    (:last current-market-value)
            diff  (- (read-string to) ((fnil read-string to) from))]
        (println currency-pair "price changed, from:" from ", to:" to ", diff:" diff))

      (swap! market-history update-in [currency-pair] #(prepend-history current-market-value %)))))


;; TODO: Hold last 1000 prices in history for each currency pair
;; TODO: report price change on async channel
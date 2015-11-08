(ns cryptocoin.market-history)

(def market-history (atom {}))

(defn last-price-changed? [currency-pair market-value]
  (let [history-price (-> @market-history currency-pair :last)
        price         (:last market-value)]
    (not= history-price price)))

(defn update-markets [current-markets]
  (doseq [[currency-pair current-market-value] current-markets]
    (when (last-price-changed? currency-pair current-market-value)
      (println currency-pair
               "price changed, from:" (-> @market-history currency-pair :last)
               ", to: " (:last current-market-value))
      (swap! market-history assoc-in [currency-pair :last] (:last current-market-value)))))

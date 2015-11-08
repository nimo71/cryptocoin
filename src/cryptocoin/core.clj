(ns cryptocoin.core
  (:require [cryptocoin.poloniex :as poloniex]))

(def markets (atom {}))

(defn -main [& args]
  (println "Starting...")

  (let [returnTicker  (poloniex/returnTicker)]
    (doseq [market returnTicker]
      (let [currency-pair (first market)
            market-value  (second market)
            last-price    (:last market-value)]
        (println currency-pair "\t" last-price))))

  (comment println "returnCurrencies: " (poloniex/returnCurrencies))

  (comment println "Ctrl-C to finish")
  (comment loop [] (recur)))
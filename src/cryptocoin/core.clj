(ns cryptocoin.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(defn -main [& args]
  (println "Hello cryptocoin!!")
  (let [res (http/get "https://poloniex.com/public?command=returnTicker")]
    (if (= 200 (:status @res))

      (let [returnTickerJson  (:body @res)
            returnTicker      (json/read-str returnTickerJson :key-fn keyword)]
        (println "returnTicker currencyPairs" (keys returnTicker)))

      (println "returnTicker response status:" (:status @res)))))
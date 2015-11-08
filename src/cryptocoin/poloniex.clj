(ns cryptocoin.poloniex
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(defn- getPoloniex [command]
  (let [res (http/get (str "https://poloniex.com/public?command=" command))]
    (if (= 200 (:status @res))

      (json/read-str (:body @res) :key-fn keyword)

      (:status @res))))

(defn returnTicker []
  (getPoloniex "returnTicker"))

(defn returnCurrencies []
  (getPoloniex "returnCurrencies"))
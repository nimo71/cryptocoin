(ns cryptocoin.poloniex
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.core.async :refer :all]))

(defn- OK [status]
  (= 200 status))

(defn- success [{:keys [error status]}]
  (and (not error) (OK status)))

(defn- get-poloniex [command period-ms]
  (let [url     (str "https://poloniex.com/public?command=" command)
        channel (chan)]

    (go-loop [start-time                    (System/currentTimeMillis)
              {:keys [body status] :as res} @(http/get url {:timeout period-ms})
              end-time                      (System/currentTimeMillis)]

      (let [response-time     (- end-time start-time)
            period-remaining  (- period-ms response-time)]
        (when (> period-remaining 0)
          (<! (timeout period-remaining))))

      (if (success res)
        (>! channel {:poloniex (keyword command)
                     :value    (json/read-str body :key-fn keyword)})

        (>! channel {:error (keyword command)
                     :value {:status status}}))

      (recur (System/currentTimeMillis)
             @(http/get url {:timeout period-ms})
             (System/currentTimeMillis)))

    channel))

(defn returnTicker [period-ms]
  (get-poloniex "returnTicker" period-ms))

(ns cryptocoin.poloniex
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.core.async :refer :all]))

(defn- OK [status]
  (= 200 status))

(defn- success [{:keys [error status]}]
  (and (not error) (OK status)))

(defn- get-poloniex-channel [command period-ms]
  (let [url    (str "https://poloniex.com/public?command=" command)
        ticker (chan)]

    (go-loop [start-time              (System/currentTimeMillis)
              {:keys [body] :as res}  @(http/get url {:timeout period-ms})
              end-time                (System/currentTimeMillis)]

      (let [response-time     (- end-time start-time)
            period-remaining  (- period-ms response-time)]
        (when (> period-remaining 0)
          (<! (timeout period-remaining))))

      (if (success res)
        (>! ticker (json/read-str body :key-fn keyword))
        (println "Error durning GET: " url))

      (recur (System/currentTimeMillis)
             @(http/get url {:timeout period-ms})
             (System/currentTimeMillis)))
    ticker))

(defn returnTicker-channel [period-ms]
  (get-poloniex-channel "returnTicker" period-ms))

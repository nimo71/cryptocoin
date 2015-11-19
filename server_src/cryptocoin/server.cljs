(ns cryptocoin.server
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [<!]]))

(def net (nodejs/require "net"))

(defn start-server [ticker-channel]
  (let [server (.createServer net (fn [socket]
                                    (go-loop [tick-event (<! ticker-channel)]
                                      (.write socket tick-event)
                                      (.pipe socket socket)
                                      (recur (<! ticker-channel)))))]

    (.listen server (1337, "127.0.0.1"))))


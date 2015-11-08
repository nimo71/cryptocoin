(defproject cryptocoin "0.1.0-SNAPSHOT"
  :description "Cryptocoin exchange tools"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145" 
                  :classifier "aot"
                  :exclusion [org.clojure/data.json]]
                 [org.clojure/data.json "0.2.6" :classifier "aot"]
                 [http-kit "2.1.18"]]

  :node-dependencies [[source-map-support "0.2.8"]
                      [autobahn "0.9.8"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.9"]]

  :clean-targets ^{:protect false} ["target"]

  :build {

          }

  :cljsbuild {
              :builds [{:id "server-dev"
                        :source-paths ["server_src"]
                        :figwheel true
                        :compiler {:main cryptocoin.core
                                   :output-to "target/server_out/cryptocoin.js"
                                   :output-dir "target/server_out"
                                   :target :nodejs
                                   :optimizations :none
                                   :source-map true }}]}
  :figwheel {}

  :main cryptocoin.core)


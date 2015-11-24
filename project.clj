(defproject cryptocoin "0.1.0-SNAPSHOT"
  :description "Cryptocoin exchange tools"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"
                  :classifier "aot"
                  :exclusion [org.clojure/data.json]]
                 [org.clojure/data.json "0.2.6" :classifier "aot"]
                 [reagent "0.5.1"]
                 [re-frame "0.5.0"]
                 [re-com "0.6.2"]
                 [secretary "1.2.3"]
                 [garden "1.2.5"]
                 [compojure "1.4.0"]
                 [ring "1.4.0"]
                 [org.clojure/core.async "0.2.371"]
                 [ws.wamp.jawampa/jawampa-core "0.4.1"]
                 [ws.wamp.jawampa/jawampa-netty "0.4.1"]
                 [io.reactivex/rxjava "1.0.16"]]


  :node-dependencies [[source-map-support "0.2.8"]
                      [autobahn "0.9.8"]]

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.4.1" :exclusions [cider/cider-nrepl]]
            [lein-garden "0.2.6"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css/compiled"]

  :figwheel {:css-dirs     ["resources/public/css"]
             :ring-handler cryptocoin.handler/handler}

  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   cryptocoin.css/screen
                     :compiler     {:output-to     "resources/public/css/compiled/screen.css"
                                    :pretty-print? true}}]}

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/cljs"]

                        :figwheel     {:on-jsload "cryptocoin.core/mount-root"}

                        :compiler     {:main                 cryptocoin.core
                                       :output-to            "resources/public/js/compiled/app.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :asset-path           "js/compiled/out"
                                       :source-map-timestamp true}}

                       {:id             "test"
                        :source-paths   ["src/cljs" "test/cljs"]
                        :notify-command ["phantomjs" "test/unit-test.js" "test/unit-test.html"]
                        :compiler       {:optimizations :whitespace
                                         :pretty-print  true
                                         :output-to     "test/js/app_test.js"
                                         :warnings      {:single-segment-namespace false}}}

                       {:id           "min"
                        :source-paths ["src/cljs"]
                        :compiler     {:main          cryptocoin.core
                                       :output-to     "resources/public/js/compiled/app.js"
                                       :optimizations :advanced
                                       :pretty-print  false}}

                       {:id           "server-dev"
                        :source-paths ["src/cljs-node"]
                        :figwheel     true
                        :compiler     {:main          cryptocoin.core
                                       :output-to     "target/server_out/cryptocoin.js"
                                       :output-dir    "target/server_out"
                                       :target        :nodejs
                                       :optimizations :none
                                       :source-map    true}}]}

  ;;:main cryptocoin.core
  )





(defproject clj-hmac "0.1.0-SNAPSHOT"
  :description "A library for cryptographically signing arbitrary Clojure data."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :profiles {:dev {:dependencies [[clj-json "0.5.0"]
                                  [org.clojure/data.codec "0.1.0"]]}})

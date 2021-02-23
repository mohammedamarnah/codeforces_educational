(defproject server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [http-kit "2.5.0"]
                 [compojure "1.6.2"]
                 [org.clojure/tools.namespace "1.0.0"]
                 [metosin/jsonista "0.2.7"]]
  :main server.core
  :repl-options {:init-ns server.core})


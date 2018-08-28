(defproject foo "0.1.0"
  :plugins [[lein-tools-deps "0.4.2-SNAPSHOT"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files        [:install :project]
                           :clojure-executables ["/usr/bin/clojure" "/usr/local/bin/clojure"]}
  )

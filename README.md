# lein-tools-deps transitive dependency exclusion bug

It seems that `lein-tools-deps` can't exclude transitive dependencies in version `0.4.2-SNAPSHOT`.

In this repo we have a `deps.edn` with:
```
{:paths []
 :deps  {clj-http {:mvn/version "3.9.1" :exclusions [clj-tuple/clj-tuple
                                                     riddley/riddley]}
         uap-clj  {:mvn/version "1.3.3" :exclusions [com.taoensso/timbre
                                                     org.clojure/clojurescript]}}}
```

`clj -Stree` output contains none of the excluded libraries:
```
org.clojure/clojure 1.9.0
  org.clojure/core.specs.alpha 0.1.24
  org.clojure/spec.alpha 0.1.143
uap-clj/uap-clj 1.3.3
  russellwhitaker/immuconf 0.3.0
    org.clojure/tools.logging 0.3.1
    environ/environ 1.1.0
  circleci/clj-yaml 0.5.6
    org.flatland/ordered 1.5.5
      org.flatland/useful 0.9.0
        org.clojure/tools.macro 0.1.1
    org.yaml/snakeyaml 1.13
clj-http/clj-http 3.9.1
  commons-codec/commons-codec 1.11
  org.apache.httpcomponents/httpasyncclient 4.1.3
    org.apache.httpcomponents/httpcore-nio 4.4.6
    commons-logging/commons-logging 1.2
  slingshot/slingshot 0.12.2
  commons-io/commons-io 2.6
  org.apache.httpcomponents/httpcore 4.4.9
  org.apache.httpcomponents/httpclient-cache 4.5.5
  org.apache.httpcomponents/httpclient 4.5.5
  potemkin/potemkin 0.4.5
  org.apache.httpcomponents/httpmime 4.5.5
```

but `lein deps :tree` contains the ones I've tried to exclude:
```
 [circleci/clj-yaml "0.5.6"]
 [clj-http "3.9.1" :exclusions [[clj-tuple] [riddley]]]
 [clojure-complete "0.2.4" :exclusions [[org.clojure/clojure]]]
 [commons-codec "1.11" :exclusions [[org.clojure/clojure]]]
 [commons-io "2.6" :exclusions [[org.clojure/clojure]]]
 [commons-logging "1.2"]
 [environ "1.1.0"]
 [org.apache.httpcomponents/httpasyncclient "4.1.3" :exclusions [[org.clojure/clojure]]]
 [org.apache.httpcomponents/httpclient-cache "4.5.5" :exclusions [[org.clojure/clojure]]]
 [org.apache.httpcomponents/httpclient "4.5.5"]
 [org.apache.httpcomponents/httpcore-nio "4.4.6"]
 [org.apache.httpcomponents/httpcore "4.4.9"]
 [org.apache.httpcomponents/httpmime "4.5.5" :exclusions [[org.clojure/clojure]]]
 [org.clojure/clojure "1.9.0"]
 [org.clojure/core.specs.alpha "0.1.24"]
 [org.clojure/spec.alpha "0.1.143"]
 [org.clojure/tools.logging "0.3.1"]
 [org.clojure/tools.macro "0.1.1"]
 [org.clojure/tools.nrepl "0.2.12" :exclusions [[org.clojure/clojure]]]
 [org.flatland/ordered "1.5.5"]
 [org.flatland/useful "0.9.0"]
 [org.yaml/snakeyaml "1.13"]
 [potemkin "0.4.5" :exclusions [[org.clojure/clojure]]]
   [clj-tuple "0.2.2"]
   [riddley "0.1.12"]
 [russellwhitaker/immuconf "0.3.0"]
   [com.taoensso/timbre "4.8.0"]
     [com.taoensso/encore "2.88.0"]
       [com.taoensso/truss "1.3.6"]
     [io.aviso/pretty "0.1.33"]
   [org.clojure/clojurescript "1.9.293"]
     [com.google.javascript/closure-compiler-unshaded "v20160911"]
       [args4j "2.0.26"]
       [com.google.code.findbugs/jsr305 "1.3.9"]
       [com.google.code.gson/gson "2.2.4"]
       [com.google.guava/guava "19.0"]
       [com.google.javascript/closure-compiler-externs "v20160911"]
       [com.google.jsinterop/jsinterop-annotations "1.0.0"]
       [com.google.protobuf/protobuf-java "2.5.0"]
     [org.clojure/data.json "0.2.6"]
     [org.clojure/google-closure-library "0.0-20160609-f42b4a24"]
       [org.clojure/google-closure-library-third-party "0.0-20160609-f42b4a24"]
     [org.clojure/tools.reader "1.0.0-beta3"]
     [org.mozilla/rhino "1.7R5"]
 [slingshot "0.12.2" :exclusions [[org.clojure/clojure]]]
 [uap-clj "1.3.3" :exclusions [[com.taoensso/timbre] [org.clojure/clojurescript]]]
```

In my exploration of this issue I've found that the excluded transitive
dependencies aren't present at the end of
`lein-tools-deps.plugin/resolve-dependencies-with-deps-edn`, which seems to be
correct behavior, but then they must get added sometime later by Leiningen
internals.

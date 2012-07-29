(ns clj-hmac.core
  (:import [java.nio ByteBuffer]
           [javax.crypto Mac KeyGenerator]
           [javax.crypto.spec SecretKeySpec]))


(def ^:dynamic *default-algorithm* "HmacMD5")
(def ^:dynamic *hash-algorithms*
  {"HmacMD5" "md5"})


(defprotocol IGenerateHmacMessage
  (msg-string [this]
    "Return a string representation of this object suitable to be HMAC'd."))

(extend-protocol IGenerateHmacMessage
  java.lang.String
  (msg-string [this] this)

  java.lang.Number
  (msg-string [this] (str this))

  clojure.lang.Keyword
  (msg-string [this] (name this))

  clojure.lang.Symbol
  (msg-string [this] (name this))

  clojure.lang.ISeq
  (msg-string [this]
    (reduce #(str %1 (msg-string %2)) "" this))
  
  clojure.lang.IPersistentSet
  (msg-string [this]
    (msg-string (sort (map msg-string this))))
  
  clojure.lang.IPersistentMap
  (msg-string [this]
    (reduce #(str %1 (first %2) (second %2))
            ""
            (sort-by first (map (fn [[k v]] [(msg-string k) (msg-string v)])
                                this)))))

(defn hmac-algorithm
  "Return a canonical string representing the hash algorithm."
  ([]
     (get *hash-algorithms* *default-algorithm*))
  ([alg]
     (get *hash-algorithms* alg)))

(defprotocol IHmacAble
  (hmac [this key alg] [this key]))

(extend-type (class (byte-array []))
  IHmacAble
  (hmac
    ([this key algorithm]
       (-> (doto (Mac/getInstance algorithm) (.init (SecretKeySpec. key algorithm)))
           (.doFinal this)))
    ([this key] (hmac this key *default-algorithm*))))

(extend-type String
  IHmacAble
  (hmac
    ([this key algorithm]
       (hmac (.getBytes this "UTF-8") key algorithm))
    ([this key]
       (hmac (.getBytes this "UTF-8") key))))

(extend-type ByteBuffer
  IHmacAble
  (hmac
    ([this key algorithm]
       (let [bytes (.remaining this)]
         (doto this (.get bytes) .reset)
         (hmac bytes key algorithm)))
    ([this key]
       (hmac this key *default-algorithm*))))
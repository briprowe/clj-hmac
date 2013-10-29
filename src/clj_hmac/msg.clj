(ns clj-hmac.msg)

(defprotocol IGenerateHmacMessage
  (msg-string [this]
    "Return a string representation of this object suitable to be HMAC'd."))

(extend-protocol IGenerateHmacMessage
  nil
  (msg-string [this] (msg-string ""))
  
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

  clojure.lang.Seqable
  (msg-string [this]
    (msg-string (seq this)))
  
  clojure.lang.IPersistentSet
  (msg-string [this]
    (msg-string (sort (map msg-string this))))
  
  clojure.lang.IPersistentMap
  (msg-string [this]
    (reduce #(str %1 (first %2) (second %2))
            ""
            (sort-by first (map (fn [[k v]] [(msg-string k) (msg-string v)])
                                this)))))


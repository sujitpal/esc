esc
===

Scala client for [ElasticSearch](http://www.elasticsearch.org/) using the tools in the [Typesafe stack](http://typesafe.com/stack) - Scala, Akka and Play.

Communication with ElasticSearch is using JSON over HTTP.

The indexing subsystem uses Akka to partition the parsing and indexing work across a bunch of Actors, and a subset of Play APIs to parse/create JSON and send POST/PUT HTTP requests.

The (upcoming) search subsystem is expected to use Play.

The documents used for development and testing are taken from the [Enron Email DataSet](http://www.cs.cmu.edu/~enron/).


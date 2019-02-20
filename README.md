# Nifi custom groovy processors

custom groovy scripts to be used as processors in Apache Nifi

## Why groovy

Several languages (including Jython) are supported for custom processor in Nifi, but Nifi is a Java application.
Groovy is the only JVM-based scripting language supported.
Performance tests show that Nifi custom processors using Groovy language largely outperform Jython, Javascript or Jruby implementations.

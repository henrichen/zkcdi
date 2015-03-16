# ZK CDI #
ZK CDI integrates [ZK Java Ajax Framework](http://www.zkoss.org) and JBoss Weld CDI RI together.

ZK CDI is based on the portable extension mechanism defined in JSR-299 of the Java EE 6 platform. It allows Java EE application developers to seamlessly leverage both ZK and CDI’s powerful set of features for developing enterprise applications.

In addition to the built in features of CDI, ZK CDI provides the following features:

  * Custom variable resolver/EL resolver - The custom variable resolver resolves CDI managed beans within zscript, an EL expression `(${...})` and ZK annotated data binding expression `(@{…})` using their EL name.

  * ZK custom scopes - In addition to built-in CDI scopes such as Session, Request, Application and Conversation this extension provides five additional ZK scopes; Desktop, Page, Execution, IdSpace and Component

  * ZK components as managed beans - This feature allows developers to inject ZK components into managed beans such as ZK composers

  * UI event handlers using ZK custom annotation and CDI provided event notification model - This feature allows developers to annotate any method with ZK custom annotation and turn it into an event handler method.

For demo and more information check out our Getting started with ZK CDI  [smalltalk](http://docs.zkoss.org/wiki/Getting_started_with_ZK_CDI)

UPDATE: There is a comprehensive article on ZK CDI on IBM Developerworks titled "[Explore the CDI programming model in ZK](https://www.ibm.com/developerworks/web/library/wa-aj-zkcdi/)"

Any feedback or questions please direct them to [ZK Forum](http://www.zkoss.org/forum)
##Id Generator Slot Widget
==
This is a Protege plugin (for Protege v3.XXX) which generates Universal Unique Identifier (UUID version 4) of the instance if it does not have one from before (here Person, Image or Document are some examples of instance). The plugins checks for the value of the identifier slot (the slot that this plugin was assigned to), if it does not have one, it generates new ID and assign it to that instance in time. New ID is generated only when a new instance is created or when the instance is visited for the fast time and the value of the identifier slot is <code>null<code>.


### How to install the plugin

- Download the plugin from https://github.com/ubbdst/id-generator-widget/releases. You would need <code> Java JDK6</code> or newer to run this plugin.

- Extract the zip file to the <code>plugins</code> folder of the Protege. If the <code>plugins</code> folder does not exist, you need to create it.

- Maybe it's needless to say but you would need to explicitly select which slot you would want this plugin to be used in the Protege frames. 

- You are ready to go. 



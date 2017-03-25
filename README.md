## Id Generator Slot Widget

This is a Protege 3.x plugin which generates universal unique identifier (UUID version 4) for the new individual (instance) if it does not have one from before. 

The plugin checks for the value of the slot that this plugin was assigned to, if it does not have one, it generates new value (ID). New ID is generated only if slot value does not exist and this happens when a new instance is created or when the instance is visited for the first time.


### How to install the plugin

- Download the plugin from https://github.com/ubbdst/id-generator-widget/releases. You would need <code> Java JDK6</code> or newer to run this plugin.

- Extract the zip file to the <code>plugins</code> folder of the Protege. If the <code>plugins</code> folder does not exist (which is unlikely), you would need to create it.

- Maybe it's needless to say, but you would need to explicitly select which slot you would want this plugin to be assigned in the Protege frames. For example, you need to select <code>UBBIdGeneratorWidget</code> for <code>identifier slot</code> which accepts <code>String</code> values.



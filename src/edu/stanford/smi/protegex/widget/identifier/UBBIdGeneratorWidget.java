package edu.stanford.smi.protegex.widget.identifier;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import java.awt.Color;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import javax.swing.JOptionPane;

/**
 * @author Hemed Ali (hemed.ruwehy@ub.uib.no)
 * @version 1.0
 * @date 18-02-2015
 * <p>
 * This class generates Universal Unique Identifier (UUID version 4) of the instance (in the protege slot) if it does not have one from before.
 */

public class UBBIdGeneratorWidget extends TextFieldWidget {
    private static final String INSTANCE_TEXT = "instance";
    private boolean isFirstLoaded = true;
    private boolean isInvalidText = false;
    private Collection instanceValues = Collections.EMPTY_LIST;
    private static final String INVALID_NAME_MSG = "Name is not unique";
    private static final String PREFIX = "http://data.ub.uib.no/";

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {

        //Check if a slot accept values of type String,
        // if it does then show this widget in the dropdown list as one of the it's options.
        boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

        return isString;
    }

    /**
     * Set value of the slot if it does not have one. (during creation)
     *
     * @param values
     **/
    @Override
    public void setValues(final Collection values) {
        //System.out.println("Setting values: " + values.toString() + " for " + getInstance().getName());
        String uniqueId = (String) CollectionUtilities.getFirstItem(values);
        if (uniqueId == null) {
            //generate unique Id
            uniqueId = UUID.randomUUID().toString();
            String instanceName = PREFIX + uniqueId;
            Instance newInstance = renameInstance(instanceName);
            setInstance(newInstance);
            setText(uniqueId);
            //getTextField().setEnabled(false);
            super.setInstanceValues();
            //isTextSet = true;
            /**newInstance.getKnowledgeBase().addFrameListener(new FrameAdapter() {
                       	@Override
    	    public void frameReplaced(FrameEvent event) {
    		Frame oldFrame = event.getFrame();
    		Frame newFrame = event.getNewFrame();
    		Slot slot = getSlot();
    		if (slot != null && slot.equals(oldFrame)) {    
    			System.out.println("Frame replaced");
                         newFrame.setVisible(true);
    		}}});**/
        }
        else {
            super.setValues(values);
        }
    }



    public void addNotify() {
        super.addNotify();
        if (isRuntime() && needsNameChange()) {
            selectAll();
        }
    }

    /**public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
     return slot.getName().equals(Model.Slot.NAME);
     }**/

    private boolean needsNameChange() {
        if (getInstance() == null) { return false;}
        boolean needsNameChange = false;
        String name = getInstance().getName();
        if (isEditable() && name != null) {
            //int index = name.lastIndexOf('_');
            //String possibleIntegerString = name.substring(index + 1);
            try {
              //  Integer.parseInt(possibleIntegerString);
                needsNameChange = true;
            } catch (Exception e) {
            }
        }
        return needsNameChange;
    }





    @Override
    public void setInstanceValues() {
        Collection values = getValues();
        if (values == null) {
            throw new IllegalArgumentException("Illegal null value for frame");
        }
        else if (values.isEmpty()) {
            throw new IllegalArgumentException("Missing name for class");
        }
        else if (values.size() > 1) {
            throw new IllegalArgumentException("Too many names for frame " + values.size());
        }
        else if (!(values.iterator().next() instanceof String)) {
            throw new IllegalArgumentException("name should be a string");
        }
        String name = (String) values.iterator().next();

        //Validate name early
        validateName(name);

        Instance i = getInstance();
        if (i.getName().equals(name)) {
            invalidateTextBox(name);
            return;
        }

        //Check if the value is hash
        String currentValue = (String) CollectionUtilities.getFirstItem(values);

        //TODO: Check if uniqueID  is hash UUID v4 and how about prefix?
        i.rename(name);
        setText(currentValue);
        super.setInstanceValues();
        markDirty(false);
    }



    private boolean isValidName(String prefix, String name) {
        String frameName = prefix + name;
        Frame currentFrame = getInstance();
        Frame frame = getKnowledgeBase().getFrame(frameName);
        boolean isDuplicate = (frame != null) && !frame.equals(currentFrame);
        boolean isValid = getKnowledgeBase().isValidFrameName(frameName, currentFrame);
        return isValid && !isDuplicate && name.length() > 0;
    }


    /**
     * Validate name
     */
    private void validateName(String text){
        if (text == null || !isValidName(PREFIX, text)) {
            String t = "Instance with name '" + text + "'" + " already exists";
            invalidateTextBox(t);
            throw new IllegalArgumentException(t);
        }
    }


    /**
     * Invalidate name
     */
    private void invalidateTextBox(String text){
        getTextField().setForeground(Color.RED);
        JOptionPane.showMessageDialog(null,
                getBoldTextLabel(text),
                INVALID_NAME_MSG,
                JOptionPane.ERROR_MESSAGE);
    }
   

    /**
     * Get text label in bold.
     */
    private String getBoldTextLabel(String inputString){
        StringBuilder s = new StringBuilder();
        s.append("<html>")
                .append("<strong style='color:red;'>")
                //.append(INVALID_NAME_MSG)
                .append(inputString)
                .append("</strong>")
                // .append("\"")
                // .append("\"")
                .append("</html>");
        return s.toString();
    }


    /***@Override
     protected void onSetText(String s) {
     System.out.println("On set text ...");
     renameURI(getInstance(), s);
     }**/


    /**
     * Execute renaming
     *
     * @param instance         - an instance to rename
     * @param currentSlotValue - a value displayed in this slot
     */
    public Instance renameInstance(String newName) {
            
        Instance instance = getInstance();

        if (newName == null || newName.isEmpty()) {
            System.out.println("Name cannot be null");
            return instance;
        }

        //Return original if name exists
        if (instance.getFrameID().getName().equals(newName)) {
            System.out.println("Name is not unique");
            return instance;
        }

        //Get the current instance
        Cls instanceClass = getCls();

        //Instance name is the same as instance URI displayed in "For Individual" slot of the Protege Desktop
        String instanceURI = instance.getName();

        //Frame name is the same as instance URI displayed in "For Individual" slot of the Protege Desktop
        //e.g http://data.ub.uib.no/Image_5
        String frameName = instance.getFrameID().getName();
        String classNamespace = instanceClass.getName();
        String className = classNamespace.replaceAll("^.+?([^/#]+)$", "$1");


        /** if (frameName.contains(INSTANCE_TEXT)) {
         // URI is OK, do nothing
         //on value change

         } else {**/
        
        //TODO: There is more logic to handle here, this is only one case.
        String newInstanceURI = frameName.replaceFirst("([A-Z][a-z]+)_[0-9]+$", "instance/$1").toLowerCase() + "/" + newName;

        /**System.out.println("===================================="
         + "\nNew Instance IRI: " + newInstanceURI
         + "\nInstance Class: " + instanceClass.getName()
         + "\nDirect Type Name: " + instance.getDirectType().getName()
         + "\nFrame ID Name: " + instance.getFrameID().getName()
         + "\nClass Name: " + className
         );
         **/

            Collection directTypes = instance.getDirectTypes();

            //Delete old instance
            //System.out.println("Deleting old instance with name: " + instance.getName());
            //instance.getKnowledgeBase().deleteInstance(instance);
 
            
           
            //Create new instance
            //Instance newInstance = this.getKnowledgeBase().createInstance(newName, directTypes);
            ///System.out.println("Created new instance with name: " + newInstance.getName());
            //instanceDisplay = new InstanceDisplay(newInstance.getProject());
            //instanceDisplay.setInstance(newInstance);
            
             
            if(!isValidName("", newName)){
                    System.out.println(newName + " is not valid frame name for " + instance.getFrameID().getName());
                    return instance;
            }

             //Instance newInstance = (Instance)instance.rename(newName);
             //System.out.println("Created new instance with name: " + newInstance.getName());
             
             
             if(!instance.isDeleted()){
                //Delete old instance
                System.out.println("Deleting old instance with name: " + instance.getName());
                //instance.getKnowledgeBase().deleteInstance(instance);
                instance.delete();
                instance.markDeleted(true);
             }


            //Create new instance
             Instance newInstance = this.getKnowledgeBase().createInstance(newName, directTypes);
             System.out.println("Created new instance with name: " + newInstance.getName());
            //instanceDisplay = new InstanceDisplay(newInstance.getProject());
            //instanceDisplay.setInstance(newInstance);

 

            //Get copy of the old one, and then rename.
            //System.out.println("Copying old instance and renaming it." + instance.getName());
            //Frame newFrame = instance.deepCopy(null, null).rename(null);


            //Delete old instance
            //System.out.println("Deleting old instance with name." + instance.getName());
            //instance.setDirectTypes(directTypes);
            //this.getKnowledgeBase().deleteInstance(instance);


            return newInstance;


            //Renaming instance?
            /**
             System.out.println("Renaming Instance...");
             instance.setDirectTypes(directTypes);
             Frame newFrame = instance.rename(newInstanceURI);
             System.out.println("New frame ID: " + newFrame.getFrameID().getName() +
             "\nNew instance: " + newFrame.getName() +
             "\nClass: " + newFrame.getClass()
             );

             //this.getInstance().rename(newInstanceURI);**/
        // }


        //instance.deepCopy(newInstance.getKnowledgeBase(), null);
        //instance.setDirectTypes(instance.getDirectTypes());
        //Commit transactio
        //frame.getKnowledgeBase().commitTransaction();  
    }

    

    /**
     * The method returns the current value displayed by the widget
     * This method is called on the value change. So the idea will be
     * to rename the instance url based on change of the identifier slot
     */

    /** @Override public Collection getValues() {
    //Get current slot value
    String currentSlotValue = this.getText();
    String currentValue = (String) CollectionUtilities.getFirstItem(instanceValues);
    //System.out.println("Instance for get values:  " + getInstance());
    //System.out.println("Instance for get texts:  " + this.getText());

    System.out.println("Current instance value: " + currentValue);
    if(currentSlotValue != null && currentSlotValue.length() > 0 && isTextSet) {
    //TODO:cmore logic here
    String className = getCls().getName().replaceAll("^.+?([^/#]+)$", "$1");
    String newURI = "http://data.ub.uib.no/instance/" + className + "/" + currentSlotValue;
    String oldURI = getInstance().getFrameID().getName();
    if (newURI.equals(oldURI)) {
    System.out.println("They are equal");
    } else  {
    System.out.println("Renaming with \nNew URI: " + newURI);
    System.out.println("Old URI: " + oldURI);

    //TODO check if newURI exist
    getInstance().getKnowledgeBase().rename(getInstance(), newURI);
    //setText(currentSlotValue);
    //setInstanceValues();
    }

    }
    return CollectionUtilities.createList(currentSlotValue);
    }**/
}



package edu.stanford.smi.protegex.widget.identifier;

import java.awt.*;
import java.util.Collection;

import com.sun.xml.internal.fastinfoset.algorithm.UUIDEncodingAlgorithm;
import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protege.widget.TextFieldWidget;

import javax.swing.*;
import java.util.Collections;
import java.util.UUID;

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
    private static final String INVALID_NAME_MSG = "Name already exists: ";
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
        System.out.println("Setting values: " + values.toString() + " for " + getInstance().getName());
        String uniqueId = (String) CollectionUtilities.getFirstItem(values);
        //instanceValues = values;
        if (uniqueId == null) {
            //generate unique Id
            uniqueId = UUID.randomUUID().toString();
            //System.out.println("isInitialized : " + IdUtils.isInitialized);
            //System.out.println("Setting instance value with ID." + uniqueId);
            //Fire renaming
            //renameInstance(this.getInstance(), uniqueId);
            //isTextSet = false;
             setText(uniqueId);
            //getTextField().setEnabled(false);
             super.setInstanceValues();
            //isTextSet = true;
        }
        else {
            super.setValues(values);
        }
    }

    //TODO:Check for the


    /**
     * Respond to
     */
    @Override
    public void setInstanceValues() {
        Collection values = getValues();
        System.out.println("Instance values: " + values.toString());
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
        String uniqueId = (String) CollectionUtilities.getFirstItem(values);

        //TODO: Check if uniqueID  is hash UUID v4
        //How about fprefix?
        if(uniqueId.startsWith("ubb")){
            i.rename(name);
            setText(uniqueId);
            super.setInstanceValues();
            markDirty(false);
        }

        //Call super set InstanceValues
        //super.setInstanceValues();

    }


    /**protected String getInvalidTextDescription(String text) {
        String invalidText = null;
        if (text == null || !isValidName(text)) {
            invalidText = "Invalid frame name";
        }
        return invalidText;
    }**/


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
            String t = "Frame with name [" + text + "]" + " already exists";
            invalidateTextBox(t);
            throw new IllegalArgumentException(t);
        }
    }


    /**
     * invalidate name
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
                .append("<strong>")
                .append(INVALID_NAME_MSG)
                .append("</strong>")
                .append("\"")
                .append(inputString)
                .append("\"")
                .append("</html>");
        return s.toString();
    }

    @Override
    public void setInstance(Instance instance) {
        super.setInstance(instance);
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
    public Instance renameInstance(Instance instance, String currentSlotValue) {
        Instance renamedInstance = instance;

        System.out.println("Instance UUID: " + currentSlotValue);
        //Get the current instance
        Cls instanceClass = getCls();

        //Instance name is the same as instance URI displayed in "For Individual" slot of the Protege Desktop
        String instanceURI = instance.getName();

        //Frame name is the same as instance URI displayed in "For Individual" slot of the Protege Desktop
        //e.g http://data.ub.uib.no/Image_5
        String frameName = instance.getFrameID().getName();
        String classNamespace = instanceClass.getName();
        String className = classNamespace.replaceAll("^.+?([^/#]+)$", "$1");


        if (currentSlotValue != null || !currentSlotValue.isEmpty()) {

            String newInstanceURI;

            if (frameName.contains(INSTANCE_TEXT)) {
                // URI is OK, do nothing
                //on value change

            } else {
                //TODO: There is more logic to handle here, this is only one case.
                newInstanceURI = frameName.replaceFirst("([A-Z][a-z]+)_[0-9]+$", "instance/$1").toLowerCase() + "/" + currentSlotValue;

                /**System.out.println("===================================="
                 + "\nNew Instance IRI: " + newInstanceURI
                 + "\nInstance Class: " + instanceClass.getName()
                 + "\nDirect Type Name: " + instance.getDirectType().getName()
                 + "\nFrame ID Name: " + instance.getFrameID().getName()
                 + "\nClass Name: " + className
                 );


                 **/


                try {
                    Collection directTypes = instance.getDirectTypes();

                    //Delete old instance
                    //System.out.println("Deleting old instance with name." + instance.getName());
                    //this.getKnowledgeBase().deleteInstance(instance);

                    //Create new instance
                    Instance newInstance = this.getKnowledgeBase().createInstance(null, directTypes);
                    System.out.println("Created new instance with name." + newInstance.getName());



                    FrameID id = new FrameID(null);
                    System.out.println("Frame ID" + id.getName());


                    //FrameID id = new FrameID(newInstanceURI);

                    //Get copy of the old one, and then rename.
                    //System.out.println("Copying old instance and renaming it." + instance.getName());
                    //Frame newFrame = instance.deepCopy(null, null).rename(null);


                    //Delete old instance
                    //System.out.println("Deleting old instance with name." + instance.getName());
                    //instance.setDirectTypes(directTypes);
                    //this.getKnowledgeBase().deleteInstance(instance);


                    return (Instance)newInstance;


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

                } catch (Exception ie) {
                    System.out.println("Strange! null class? I don't know how [" + ie.getLocalizedMessage() + "]");
                }
            }


                //instance.deepCopy(newInstance.getKnowledgeBase(), null);
                //instance.setDirectTypes(instance.getDirectTypes());
                //Commit transactio
                //frame.getKnowledgeBase().commitTransaction();

                // }**/
            }
            return instance;

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



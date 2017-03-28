package edu.stanford.smi.protegex.widget.identifier;

import java.util.Collection;

import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protege.widget.TextFieldWidget;

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

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {

        //Check if a slot accept values of type String,
        // if it does then show this widget in the dropdown list as one of the it's options.
        boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

        return isString;
    }

    /**
     * Set value of the slot if it does not have one.
     *
     * @param values
     **/
    @Override
    public void setValues(final Collection values) {
        System.out.println("Instance: " + getInstance().getName());
        String uniqueId = (String) CollectionUtilities.getFirstItem(values);
        if (uniqueId == null) {
            //generate unique Id
            uniqueId = UUID.randomUUID().toString();
            setText(uniqueId);
            //getTextField().setEnabled(false);
           //this.setInstanceValues();
            //System.out.println("Setting instance value with ID." + uniqueId);
            //Fire renaming
            //renameURI(this.getInstance(), uniqueId);

            Instance instance = getInstance();
            //Get current slot value
            String currentSlotValue = this.getText();

            System.out.println("Instance for get values:  " + getInstance());
            System.out.println("Instance for get texts:  " + this.getText());

            //TODO: //more logic here
            String className = getCls().getName().replaceAll("^.+?([^/#]+)$", "$1");

            String newURI = "http://data.ub.uib.no/instance/" + className + "/" + currentSlotValue;
            String oldURI = getInstance().getFrameID().getName();
        }
        else {
            super.setValues(values);
        }

        //Fire renaming
        //renameInstance(this.getInstance(), uniqueId);

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

                 Collection directTypes = instance.getDirectTypes();
                 **/


                try {

                    //Delete old instance
                    //System.out.println("Deleting old instance with name." + instance.getName());
                    //this.getKnowledgeBase().deleteInstance(instance);

                    //Create new instance
                    //Instance newInstance = this.getKnowledgeBase().createInstance(newInstanceURI, directTypes);
                    //System.out.println("Created new instance with name." + newInstance.getName());

                    //FrameID id = new FrameID(newInstanceURI);
                    Collection directTypes = instance.getDirectTypes();


                    //Get copy of the old one, and then rename.
                    System.out.println("Copying old instance and renaming it." + instance.getName());
                    Frame newFrame = instance
                            .deepCopy(null, null)
                            .rename(newInstanceURI);

                    //Delete old instance
                    System.out.println("Deleting old instance with name." + instance.getName());
                    instance.setDirectTypes(directTypes);
                    this.getKnowledgeBase().deleteInstance(instance);


                    return (Instance) newFrame;


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

        @Override public Collection getValues() {
        //Get the current instance
        Instance instance = getInstance();
        //Get current slot value
        String currentSlotValue = this.getText();

        System.out.println("Instance for get values:  " + getInstance());
        System.out.println("Instance for get texts:  " + this.getText());

        //TODO: //more logic here
        String className = getCls().getName().replaceAll("^.+?([^/#]+)$", "$1");

        String newURI = "http://data.ub.uib.no/instance/" + className + "/" + currentSlotValue;
        String oldURI = getInstance().getFrameID().getName();


        if(newURI.equals(oldURI)){
            System.out.println("They are equal");
        }
        else {
            System.out.println("New URI: " + newURI);
            System.out.println("Old URI: " + oldURI);
            //Rename
            //getInstance().rename(newURI);
            getInstance().getKnowledgeBase().rename(getInstance(), newURI);
            //TODO check if newURI exist

        }
        //Fire renaming
        //Instance newInstance = renameInstance(this.getInstance(), getText());
        //setInstance(newInstance);


        if(currentSlotValue == null){
        System.out.println("Got null slot value ");
        }

        return CollectionUtilities.createList(currentSlotValue);
        }
    }



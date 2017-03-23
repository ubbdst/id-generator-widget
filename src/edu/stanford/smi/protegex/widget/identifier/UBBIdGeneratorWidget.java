package edu.stanford.smi.protegex.widget.identifier;

import java.util.Collection;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import java.util.UUID;

/**
 * @author Hemed Ali (hemed.ruwehy@ub.uib.no)
 * @version 1.0
 * @date 18-02-2015
 * 
 * This class generates Universal Unique Identifier (UUID version 4) of the instance (in the protege slot) if it does not have one from before.
 */

public class UBBIdGeneratorWidget extends TextFieldWidget {
    private static final String INSTANCE_TEXT = "instance" ;
	public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
            
           //Check if a slot accept values of type String, 
          // if it does then show this widget in the dropdown list as one of the it's options.
           boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;
           
           return isString;
	}

       /**
        * Set value of the slot if it does not have one.
        * @param values
        **/
      @Override
        public void setValues(final Collection values){
              String uniqueId = (String)CollectionUtilities.getFirstItem(values);
              if (uniqueId == null){ 
                //generate unique Id
                uniqueId = UUID.randomUUID().toString();
                setText(uniqueId);
                //getTextField().setEnabled(false);
                setInstanceValues();
              }
           else 
             super.setValues(values);
        }
        
        
   /**
      The method returns the current value displayed by the widget
      This method is called on the value change. So the idea will be
      to rename the instance url based on change of the identifier slot
   */
   @Override
      public Collection getValues() {
        //Get the current instance
         Instance instance = getInstance();
         Cls instanceClass = getCls();
         
         //Instance name is the same as instance URI displayed in "For Individual" slot of the Protege Desktop
         String instanceURI = instance.getName();

         //Frame name is the same as instance URI displayed in "For Individual" slot of the Protege Desktop
         //e.g http://data.ub.uib.no/Image_5
         String frameName = instance.getFrameID().getName();

         
         String classNamespace = instanceClass.getName();
         
         String className =  classNamespace.replaceAll("^.+?([^/#]+)$", "$1");

         //Get the current value from the slot widget
         String currentSlotValue = getText();

         if(!currentSlotValue.isEmpty()){
             String newInstanceURI;
             if(frameName.contains(INSTANCE_TEXT)){
                 // URI is OK, just append identifier at the of the URI
             }
             else {
                 //TODO: There is more logic to handle here, this is only one case.
                 newInstanceURI = frameName.replaceFirst("([A-Z][a-z]+)_[0-9]+$", "instance/$1").toLowerCase() + "/" + currentSlotValue;
                 System.out.println("===================================="
                         + "\nNew Instance IRI: " + newInstanceURI
                         + "\nInstance Class: " + instanceClass.getName()
                         + "\nDirect Type Name: " + instance.getDirectType().getName()
                         + "\nFrame ID Name: " + instance.getFrameID().getName()
                         + "\nClass Name: " + className
                 );

                 //Renaming instance?
                 Frame frame = instance.rename(newInstanceURI);
                 //Commit transaction
                 frame.getKnowledgeBase().commitTransaction();
                 System.out.println("New frame ID: " + frame.getFrameID().getName());


                 //instance.getKnowledgeBase().rename(instance.getKnowledgeBase().getFrame(instance.getFrameID()), newInstanceURI);

                 //Commit renaming
                 //frame.getKnowledgeBase().commitTransaction();
             }

             /*
             /*
                 Output:
                 New Instance IRI: http://data.ub.uib.no/instance/image/ae41ce80-4f61-4ce5-b448-57f8d7a6babd
                 Instance Class: http://purl.org/dc/terms/Image
                 Direct Type Name: http://purl.org/dc/terms/Image
                 Frame ID Name: http://data.ub.uib.no/Image_5
                 Class Name: Image
             */
              //TODO: We want to rename instance URI here.
              //System.out.println("Renaming Instance...");
              //instance.rename(currentSlotValue);
         }

         return CollectionUtilities.createCollection(currentSlotValue);
         }
        
}


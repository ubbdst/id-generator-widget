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
        
	public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
          
            //check if a slot accept values of type String, 
           // if it does then show this widget in the dropdown list as one of the it's options.
           boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

           return isString;
	}
        
       /**
        * Set value of the slot if it does not have one.
        * @param values
        **/
      @Override
        public void setValues(final Collection values) {
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
        
               
        /*The method returns the current value displayed by the widget
         This method is called on the value change. So the idea will be
         to rename the instance url based on change of the identifier slot
        */
        @Override
            public Collection getValues() {
                //Get the current instance
                Instance instance = getInstance();
                
                //Instance name is the same as instance URI deplayed in "For Individual" slot of the Protege Desktop
                String instanceURI = instance.getName();
                          
                //TO DO: There is more logic to handle here, this is only one case.
                String newURI = instanceURI.replaceFirst("([A-Z][a-z]+)_[0-9]+$","instance/$1").toLowerCase(); 
                
                //get the current value fron the slot widget
                String currentSlotValue = getText();
                
                if(!currentSlotValue.isEmpty()){
                 
                 //TO DO: We want to rename instance URI here.
                 System.out.println("====================================\n"
                                + "New Value IRI: " + newURI + "/" + currentSlotValue);
                }
                return CollectionUtilities.createCollection(currentSlotValue);
            }
        
}

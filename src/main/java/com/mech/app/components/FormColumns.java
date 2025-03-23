package com.mech.app.components;

import com.vaadin.flow.component.formlayout.FormLayout;

public class FormColumns{

    private FormLayout formLayout = new FormLayout();

    public FormColumns(FormLayout formlayout){
        this.formLayout = formlayout;
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        this.formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("360px", 2));
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("500px", 3));
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("760px", 4));
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("900px", 5));
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("1100px", 6));
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("1200px", 7));
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("1500px", 8));
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("1700px", 9));
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("1900", 10));
    }
}//end of class...

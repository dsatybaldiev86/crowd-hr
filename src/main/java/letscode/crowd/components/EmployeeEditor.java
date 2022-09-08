package letscode.crowd.components;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import letscode.crowd.domain.Employee;
import letscode.crowd.repo.EmployeeRepo;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;



@SpringComponent
@UIScope
public class EmployeeEditor extends VerticalLayout implements KeyNotifier {
       private final EmployeeRepo employeeRepo;

       private Employee employee;

       private TextField firstName = new TextField("First Name");
       private TextField lastName = new TextField("Last Name");
       private TextField patronymic = new TextField("Patronymic");

       private Button save = new Button("Save", VaadinIcon.CHECK.create());
       private Button cancel = new Button("Cancel");
       private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
       private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

       private Binder<Employee> binder = new Binder<>(Employee.class);
       @Setter
       private ChangeHandler changeHandler;
       public interface ChangeHandler{
           void onChange();
       }

    @Autowired
    public EmployeeEditor(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
        add(firstName, lastName, patronymic, actions);
        binder.bindInstanceFields(this);
        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editEmployee(employee));
        setVisible(false);
    }
    public void save() {
           employeeRepo.save(employee);
           changeHandler.onChange();
    }
    public void delete() {
        employeeRepo.delete(employee);
        changeHandler.onChange();
    }
    public void editEmployee(Employee newEmployee) {
           if(newEmployee == null){
               setVisible(false);
               return;
           }
           if(newEmployee.getId() != null){
               employee = employeeRepo.findById(newEmployee.getId()).orElse(newEmployee);
           }else {
               employee = newEmployee;
           }

           binder.setBean(employee);
           setVisible(true);

           lastName.focus();
    }
}

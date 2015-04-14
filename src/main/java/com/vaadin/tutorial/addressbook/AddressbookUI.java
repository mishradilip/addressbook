package com.vaadin.tutorial.addressbook;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;
import java.util.Arrays;

/* The user interface class.
 * This is the user interface that is displayed in the browser.
 * New instance of this class is created for every user that browses
 * to the application URL. Note that class variables are session scoped.
 *
 */
@Title("Addressbook")
@Theme("valo")
public class AddressbookUI extends UI {


	// ContactService mimics a real world DAO, that you'd typically implement as
	// EJB or Spring Data based service.
	private ContactService service = ContactService.createDemoService();

	/* Built-in and custom components.
	 * Import the default Vaadin components from in com.vaadin.ui package.
	 * ContactForm is our own reusable component.
	 */
	private TextField filter = new TextField();

	private Button newContact = new Button("New contact");

	private Table contactList = new Table();

	private ContactForm contactForm = new ContactForm(this);

	/* The "main method" for Vaadin
	 * This is the entry point method executed to initialize and configure
	 * the visible user interface.
	 *
	 * Use built-in Vaadin components, build your own, or import add-ons
	 * form vaadin.com/directory.
	 *
	 */
	@Override
	protected void init(VaadinRequest request) {

		filter.setInputPrompt("Filter contacts...");

		contactList.setSelectable(true);

		/* Receive user events.
		 * With Vaadin you program completely in event-driven way.
		 * Receive user interaction events and emitting your own.
		 */
		newContact.addClickListener((Button.ClickEvent e) -> editContact(new Contact()));

		filter.addTextChangeListener((TextChangeEvent e) -> listContacts(e.getText()));

		contactList.addValueChangeListener((Property.ValueChangeEvent e)
						-> 	editContact((Contact) e.getProperty().getValue()));


		/* Build the main layout.
		 * Layouts are components that you can put other components in.
		 * Here we use  HorizontalLayout for filter and new actions
		 * and wrap them and contactList to VerticalLayout.
		 * With a SplitPanel you can allow user to resize the components.
		 * Here split is between the ContactForm and left side content.
		 */
		HorizontalLayout actions = new HorizontalLayout(filter, newContact);
		actions.setWidth("100%");
		filter.setWidth("100%");
		actions.setExpandRatio(filter, 1);

		VerticalLayout left = new VerticalLayout(actions, contactList);
		left.setSizeFull();
		contactList.setSizeFull();
		left.setExpandRatio(contactList, 1);

		// Split to allow resizing
		setContent(new HorizontalSplitPanel(left, contactForm));

		// List initial content from the back-end data source
		listContacts();
	}

	/* Embrace clean code.
	 * It is good practice to have separate data access methods that
	 * handle the
	 *
	 */
	private void listContacts() {
		listContacts(filter.getValue());
	}

	private void listContacts(String text) {
		contactList.setContainerDataSource(new BeanItemContainer<>(
				Contact.class, service.findAll(text)), Arrays.asList(
				"firstName", "lastName", "email"));
		contactList.setColumnHeaders("First name", "Last name", "email");
		contactForm.setVisible(false);
	}

	private void editContact(Contact contact) {
		if (contact != null) {
			contactForm.edit(contact);
		} else {
			contactForm.setVisible(false);
		}
	}

	/*
	 * These methods are called by ContactForm when user wants to
	 * persist or reset changes to the edited contact.
	 */
	public void save(Contact contact) {
		service.save(contact);
		listContacts();
	}

	public void deselect() {
		contactList.setValue(null);
	}

	/* Define the application URI.
	 *
	 *  Vaadin applications are basically just Serlvlets, and you can specify here
	 *  additional parameters like the URI and UI class name and turn on production mode.
	 *
	 */
	@WebServlet(urlPatterns = "/*")
	@VaadinServletConfiguration(ui = AddressbookUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}


}

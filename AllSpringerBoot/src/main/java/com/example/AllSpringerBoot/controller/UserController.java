package com.example.AllSpringerBoot.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.AllSpringerBoot.dao.ContactRepository;
import com.example.AllSpringerBoot.dao.MainRepository;
import com.example.AllSpringerBoot.entity.Contact;
import com.example.AllSpringerBoot.entity.User;
import com.example.AllSpringerBoot.helper.MsgHelper;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bcpy;
	
	@Autowired
	private ContactRepository cpr;

	@Autowired
	private MainRepository pmr;

	@ModelAttribute
	public void addCommonData(Model m, Principal pri) {
		String username = pri.getName();

		User user = pmr.getUserByName(username);
		
		m.addAttribute("uu", user);
		
		System.out.println(".I am user" + user);
	}

	@RequestMapping("/udash")
	public String dashboard(Model m, Principal pri) {
		m.addAttribute("title", "User Dashboard");
		return "normal/user_dash";
	}

	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {
		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// constact form form insertion
	@PostMapping("/process-contact")
	public String insertCOntact(@ModelAttribute Contact contact, @RequestParam("gimage") MultipartFile file,
			Principal pri, HttpSession session) {
		try {
			// System.out.println("Data of ocntact"+contact);
			String name = pri.getName();

			User userName = this.pmr.getUserByName(name);

			// process image file
			if (file.isEmpty()) {
				contact.setImage("download.png");

			} else {
				contact.setImage(file.getOriginalFilename());
				File filer = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(filer.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("message", new MsgHelper("Contact has been added successfully !!", "success"));
			}

			userName.getCotacts().add(contact);
			contact.setUser(userName);

			this.pmr.save(userName);

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new MsgHelper("Something went wrong try agains !!", "success"));
		}
		return "normal/add_contact_form";
	}

	// show contacts
	// perpage 4 records
	// current page 0[page]
	@RequestMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") int page, Model m, Principal pri) {
		// String userName=pri.getName();
		// User us=this.pmr.getUserByName(userName);

		// List<Contact> contacts=us.getCotacts();
		String userName = pri.getName();
		User uuu = this.pmr.getUserByName(userName);

		Pageable pageable = PageRequest.of(page, 2);

		Page<Contact> contacts = this.cpr.findContactsByUser(uuu.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentpage", page);
		m.addAttribute("totalpage", contacts.getTotalPages());

		System.out.println(contacts);
		return "normal/show_contact";
	}

	// perticular person contact

	@RequestMapping("/contact/{cid}")
	public String showPersonDetail(@PathVariable("cid") Integer cid, Model m, Principal pri) {

		Optional<Contact> contactOpt = this.cpr.findById(cid);
		Contact conta = contactOpt.get();

		// security concern
		String userName = pri.getName();
		User user = this.pmr.getUserByName(userName);

		if (user.getId() == conta.getUser().getId()) {
			m.addAttribute("conta", conta);
			m.addAttribute("title", conta.getName());

		}
		return "normal/contactDetails";

	}

	@GetMapping("/dalta/{sid}")
	public String dalterData(@PathVariable("sid") Integer sid, Principal pri) {

		Optional<Contact> contactOpt = this.cpr.findById(sid);
		Contact conta = this.cpr.findById(sid).get();

		// conta.setUser(null);
		User user = this.pmr.getUserByName(pri.getName());

		user.getCotacts().remove(conta);

		this.pmr.save(user);
		// this.cpr.delete(conta);
		// this.cpr.deleteById(conta.getCid());
		System.out.println("----------->" + conta.getCid());

		return "redirect:/user/show-contacts/0";
	}

	// for update
	@PostMapping("/update-contact/{cid}")
	public String updateFormDetail(@PathVariable("cid") Integer uids, Model m) {
		m.addAttribute("title", "Update Contact");
		Contact updateContact = this.cpr.findById(uids).get();
		m.addAttribute("contact", updateContact);
		return "normal/update-form";
	}

	// process update
	@PostMapping(value = "/process-contact-update")
	public String processUpdateDetail(@ModelAttribute Contact contact, @RequestParam("gimage") MultipartFile file,
			Principal pri, HttpSession session) {
		try {
			Contact oldcontactdetail=this.cpr.findById(contact.getCid()).get();

			if (!file.isEmpty()) {
				//delete old photo
				
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file2=new File(deleteFile,oldcontactdetail.getImage());
				file2.delete();
				
				//update new photo
				
				File filer = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(filer.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
			} else {
				contact.setImage(oldcontactdetail.getImage());
			}
			
			User user=this.pmr.getUserByName(pri.getName());
			contact.setUser(user);
			this.cpr.save(contact);
			
			session.setAttribute("message", new MsgHelper("Contact updated successfully ...", "success"));
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		
		
		return "redirect:/user/contact/"+contact.getCid();
	}
	
	@GetMapping("/dractf")
	public String getProfileView(Model m) {
		m.addAttribute("title","See Profile");
		return "normal/drafter";
	}
	
	//settings
	@GetMapping("/setting")
	public String handleSettings(Model m) {
		m.addAttribute("title","Settings");
		return "normal/setting";
	}
	//change password
	@PostMapping("/changepass")
	public String changepasses(@RequestParam("old")String old,@RequestParam("new")String newer, Principal pri,HttpSession session) {
		
		String username=pri.getName();
		User currentUser=this.pmr.getUserByName(username);
		
		if(this.bcpy.matches(old, currentUser.getPassword())) {
			currentUser.setPassword(this.bcpy.encode(newer));
			this.pmr.save(currentUser);
			session.setAttribute("message", new MsgHelper("Password updated succesfully", "success"));
		}else {
			session.setAttribute("message", new MsgHelper("Please enter correct password", "danger"));
			return"redirect:/user/setting";
		}
		return"redirect:/user/udash";
	}
}

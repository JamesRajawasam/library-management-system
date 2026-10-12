package com.nsbm.library.controller;

import com.nsbm.library.model.Book;
import com.nsbm.library.repository.BookRepository;
import com.nsbm.library.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookController(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listBooks(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("books", bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("books", bookRepository.findAll());
        }
        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryRepository.findAll());
        return "books/form";
    }

    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute("book") Book book, BindingResult result, Model model) {
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            result.rejectValue("availableCopies", "error.book", "Available copies cannot exceed total copies");
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "books/form";
        }

        bookRepository.save(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryRepository.findAll());
        return "books/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookRepository.deleteById(id);
        return "redirect:/books";
    }
}
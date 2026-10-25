package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/bags")
public class BagController {

	private final List<Bag> bags = new ArrayList<>(List.of(
			new Bag(1L, "トートバッグ", 3000),
			new Bag(2L, "リュック", 8000)));
	private final AtomicLong nextId = new AtomicLong(3);

	// ステップ1: @RequestParam  例) /bags?maxPrice=5000
	@GetMapping
	public List<Bag> list(@RequestParam(required = false) Integer maxPrice) {
		if (maxPrice == null)
			return bags;
		return bags.stream().filter(b -> b.price() <= maxPrice).toList();
	}

	// ステップ1: @PathVariable  例) /bags/1
	@GetMapping("/{id}")
	public Bag get(@PathVariable Long id) {
		return bags.stream().filter(b -> b.id().equals(id)).findFirst().orElse(null);
	}
//
	// ステップ3: @PostMapping + @RequestBody
	@PostMapping
	public Bag create(@RequestBody Bag req) {
		Bag bag = new Bag(nextId.getAndIncrement(), req.name(), req.price());
		bags.add(bag);
		return bag;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		boolean removed = bags.removeIf(b -> b.id().equals(id));
		if (!removed) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND); // ないIDなら404

		}
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Bag put(@PathVariable Long id, @RequestBody Bag req) {
		for (int i = 0; i < bags.size(); i++) {
			if (bags.get(i).id().equals(id)) {
				Bag updated = new Bag(id, req.name(), req.price()); // idはURLのものを使う
				bags.set(i, updated); // i番目を置き換え
				return updated;
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 見つからなければ404
	}
}
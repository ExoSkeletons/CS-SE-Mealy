package com.eanie.mealy.data;

import java.util.Objects;
import java.util.function.Function;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class ItemKeyCallback<T, F> extends DiffUtil.ItemCallback<T> {
	private final Function<? super T, ? extends F> keyExtractor;


	public ItemKeyCallback(Function<? super T, ? extends F> keyExtractor) {
		this.keyExtractor = keyExtractor;
	}

	@Override
	public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
		return Objects.equals(keyExtractor.apply(oldItem), keyExtractor.apply(newItem));
	}

	@Override
	public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
		return Objects.equals(oldItem, newItem);
	}
}

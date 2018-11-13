/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.apio.architect.internal.routes;

import static com.liferay.apio.architect.internal.unsafe.Unsafe.unsafeCast;

import com.liferay.apio.architect.alias.form.FormBuilderFunction;
import com.liferay.apio.architect.alias.routes.CustomItemFunction;
import com.liferay.apio.architect.alias.routes.DeleteItemConsumer;
import com.liferay.apio.architect.alias.routes.GetItemFunction;
import com.liferay.apio.architect.alias.routes.UpdateItemFunction;
import com.liferay.apio.architect.alias.routes.permission.HasRemovePermissionFunction;
import com.liferay.apio.architect.alias.routes.permission.HasUpdatePermissionFunction;
import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.consumer.throwable.ThrowableBiConsumer;
import com.liferay.apio.architect.consumer.throwable.ThrowableConsumer;
import com.liferay.apio.architect.consumer.throwable.ThrowablePentaConsumer;
import com.liferay.apio.architect.consumer.throwable.ThrowableTetraConsumer;
import com.liferay.apio.architect.consumer.throwable.ThrowableTriConsumer;
import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.custom.actions.CustomRoute;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.function.throwable.ThrowableBiFunction;
import com.liferay.apio.architect.function.throwable.ThrowableFunction;
import com.liferay.apio.architect.function.throwable.ThrowableHexaFunction;
import com.liferay.apio.architect.function.throwable.ThrowablePentaFunction;
import com.liferay.apio.architect.function.throwable.ThrowableTetraFunction;
import com.liferay.apio.architect.function.throwable.ThrowableTriFunction;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.single.model.SingleModelImpl;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.single.model.SingleModel;

import io.vavr.CheckedRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alejandro Hernández
 */
public class ItemRoutesImpl<T, S> implements ItemRoutes<T, S> {

	public ItemRoutesImpl(BuilderImpl<T, S> builderImpl) {
		_actionSemantics = builderImpl._actionSemantics;
	}

	/**
	 * Returns the list of {@link ActionSemantics} created by a {@link Builder}.
	 *
	 * @review
	 */
	public List<ActionSemantics> getActionSemantics() {
		return _actionSemantics;
	}

	@Override
	public Optional<Map<String, CustomItemFunction<?, S>>>
		getCustomItemFunctionsOptional() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, CustomRoute> getCustomRoutes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<DeleteItemConsumer<S>> getDeleteConsumerOptional() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Form> getFormOptional() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<GetItemFunction<T, S>> getItemFunctionOptional() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<UpdateItemFunction<T, S>> getUpdateItemFunctionOptional() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates the {@code ItemRoutes} of an {@link
	 * com.liferay.apio.architect.router.ItemRouter}.
	 *
	 * @param <T> the model's type
	 * @param <S> the type of the model's identifier (e.g., {@code Long}, {@code
	 *        String}, etc.)
	 */
	public static class BuilderImpl<T, S> implements Builder<T, S> {

		public BuilderImpl(
			Item item, Supplier<Form.Builder> formBuilderSupplier,
			Function<String, Optional<String>> nameFunction) {

			_item = item;
			_formBuilderSupplier = formBuilderSupplier;
			_nameFunction = nameFunction;
		}

		@Override
		public <R, U, I extends Identifier<?>> Builder<T, S> addCustomRoute(
			CustomRoute customRoute,
			ThrowableBiFunction<S, R, U> throwableBiFunction, Class<I> supplier,
			BiFunction<Credentials, S, Boolean> permissionBiFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Id.class, bodyClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> throwableBiFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					_getId(params.get(0)),
					_getModel(form, () -> (Body)params.get(1))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D, R, U, I extends Identifier<?>> Builder<T, S>
			addCustomRoute(
				CustomRoute customRoute,
				ThrowableHexaFunction<S, R, A, B, C, D, U>
					throwableHexaFunction,
				Class<A> aClass, Class<B> bClass, Class<C> cClass,
				Class<D> dClass, Class<I> supplier,
				BiFunction<Credentials, S, Boolean> permissionBiFunction,
				FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Id.class, bodyClass, aClass, bClass, cClass, dClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> throwableHexaFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					_getId(params.get(0)),
					_getModel(form, () -> (Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4)), unsafeCast(params.get(5))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, C, R, U, I extends Identifier<?>> Builder<T, S>
			addCustomRoute(
				CustomRoute customRoute,
				ThrowablePentaFunction<S, R, A, B, C, U> throwablePentaFunction,
				Class<A> aClass, Class<B> bClass, Class<C> cClass,
				Class<I> supplier,
				BiFunction<Credentials, S, Boolean> permissionBiFunction,
				FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Id.class, bodyClass, aClass, bClass, cClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> throwablePentaFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					_getId(params.get(0)),
					_getModel(form, () -> (Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, R, U, I extends Identifier<?>> Builder<T, S>
			addCustomRoute(
				CustomRoute customRoute,
				ThrowableTetraFunction<S, R, A, B, U> throwableTetraFunction,
				Class<A> aClass, Class<B> bClass, Class<I> supplier,
				BiFunction<Credentials, S, Boolean> permissionBiFunction,
				FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Id.class, bodyClass, aClass, bClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> throwableTetraFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					_getId(params.get(0)),
					_getModel(form, () -> (Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, R, U, I extends Identifier<?>> Builder<T, S> addCustomRoute(
			CustomRoute customRoute,
			ThrowableTriFunction<S, R, A, U> throwableTriFunction,
			Class<A> aClass, Class<I> supplier,
			BiFunction<Credentials, S, Boolean> permissionBiFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Id.class, bodyClass, aClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> throwableTriFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					_getId(params.get(0)),
					_getModel(form, () -> (Body)params.get(1)),
					unsafeCast(params.get(2))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A> Builder<T, S> addGetter(
			ThrowableBiFunction<S, A, T> getterThrowableBiFunction,
			Class<A> aClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Id.class, aClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> getterThrowableBiFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), unsafeCast(params.get(1))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public Builder<T, S> addGetter(
			ThrowableFunction<S, T> getterThrowableFunction) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Id.class
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> getterThrowableFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D> Builder<T, S> addGetter(
			ThrowablePentaFunction<S, A, B, C, D, T>
				getterThrowablePentaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			Class<D> dClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Id.class, aClass, bClass, cClass, dClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> getterThrowablePentaFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), unsafeCast(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C> Builder<T, S> addGetter(
			ThrowableTetraFunction<S, A, B, C, T> getterThrowableTetraFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Id.class, aClass, bClass, cClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> getterThrowableTetraFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), unsafeCast(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B> Builder<T, S> addGetter(
			ThrowableTriFunction<S, A, B, T> getterThrowableTriFunction,
			Class<A> aClass, Class<B> bClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Id.class, aClass, bClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> getterThrowableTriFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), unsafeCast(params.get(1)),
					unsafeCast(params.get(2))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A> Builder<T, S> addRemover(
			ThrowableBiConsumer<S, A> removerThrowableBiConsumer,
			Class<A> aClass,
			HasRemovePermissionFunction<S> hasRemovePermissionFunction) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"remove"
			).method(
				"DELETE"
			).receivesParams(
				Id.class, aClass
			).returnsNothing(
			).notAnnotated(
			).executeFunction(
				params -> _run(
					() -> removerThrowableBiConsumer.accept(
						_getId(params.get(0)), unsafeCast(params.get(1))))
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public Builder<T, S> addRemover(
			ThrowableConsumer<S> removerThrowableConsumer,
			HasRemovePermissionFunction<S> hasRemovePermissionFunction) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"remove"
			).method(
				"DELETE"
			).receivesParams(
				Id.class
			).returnsNothing(
			).notAnnotated(
			).executeFunction(
				params -> _run(
					() -> removerThrowableConsumer.accept(
						_getId(params.get(0))))
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D> Builder<T, S> addRemover(
			ThrowablePentaConsumer<S, A, B, C, D> removerThrowablePentaConsumer,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			HasRemovePermissionFunction<S> hasRemovePermissionFunction) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"remove"
			).method(
				"DELETE"
			).receivesParams(
				Id.class, aClass, bClass, cClass, dClass
			).returnsNothing(
			).notAnnotated(
			).executeFunction(
				params -> _run(
					() -> removerThrowablePentaConsumer.accept(
						_getId(params.get(0)), unsafeCast(params.get(1)),
						unsafeCast(params.get(2)), unsafeCast(params.get(3)),
						unsafeCast(params.get(4))))
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C> Builder<T, S> addRemover(
			ThrowableTetraConsumer<S, A, B, C> removerThrowableTetraConsumer,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			HasRemovePermissionFunction<S> hasRemovePermissionFunction) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"remove"
			).method(
				"DELETE"
			).receivesParams(
				Id.class, aClass, bClass, cClass
			).returnsNothing(
			).notAnnotated(
			).executeFunction(
				params -> _run(
					() -> removerThrowableTetraConsumer.accept(
						_getId(params.get(0)), unsafeCast(params.get(1)),
						unsafeCast(params.get(2)), unsafeCast(params.get(3))))
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B> Builder<T, S> addRemover(
			ThrowableTriConsumer<S, A, B> removerThrowableTriConsumer,
			Class<A> aClass, Class<B> bClass,
			HasRemovePermissionFunction<S> hasRemovePermissionFunction) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"remove"
			).method(
				"DELETE"
			).receivesParams(
				Id.class, aClass, bClass
			).returnsNothing(
			).notAnnotated(
			).executeFunction(
				params -> _run(
					() -> removerThrowableTriConsumer.accept(
						_getId(params.get(0)), unsafeCast(params.get(1)),
						unsafeCast(params.get(2))))
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <R> Builder<T, S> addUpdater(
			ThrowableBiFunction<S, R, T> updaterThrowableBiFunction,
			HasUpdatePermissionFunction<S> hasUpdatePermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"replace"
			).method(
				"PUT"
			).receivesParams(
				Id.class, Body.class
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> updaterThrowableBiFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D, R> Builder<T, S> addUpdater(
			ThrowableHexaFunction<S, R, A, B, C, D, T>
				updaterThrowableHexaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			HasUpdatePermissionFunction<S> hasUpdatePermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"replace"
			).method(
				"PUT"
			).receivesParams(
				Id.class, Body.class, aClass, bClass, cClass, dClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> updaterThrowableHexaFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4)), unsafeCast(params.get(5))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C, R> Builder<T, S> addUpdater(
			ThrowablePentaFunction<S, R, A, B, C, T>
				updaterThrowablePentaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			HasUpdatePermissionFunction<S> hasUpdatePermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"replace"
			).method(
				"PUT"
			).receivesParams(
				Id.class, Body.class, aClass, bClass, cClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> updaterThrowablePentaFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, R> Builder<T, S> addUpdater(
			ThrowableTetraFunction<S, R, A, B, T> updaterThrowableTetraFunction,
			Class<A> aClass, Class<B> bClass,
			HasUpdatePermissionFunction<S> hasUpdatePermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"replace"
			).method(
				"PUT"
			).receivesParams(
				Id.class, Body.class, aClass, bClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> updaterThrowableTetraFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, R> Builder<T, S> addUpdater(
			ThrowableTriFunction<S, R, A, T> updaterThrowableTriFunction,
			Class<A> aClass,
			HasUpdatePermissionFunction<S> hasUpdatePermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_item
			).name(
				"replace"
			).method(
				"PUT"
			).receivesParams(
				Id.class, Body.class, aClass
			).returns(
				SingleModel.class
			).notAnnotated(
			).executeFunction(
				params -> updaterThrowableTriFunction.andThen(
					t -> new SingleModelImpl<>(t, _item.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1)),
					unsafeCast(params.get(2))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public ItemRoutes<T, S> build() {
			return new ItemRoutesImpl<>(this);
		}

		@SuppressWarnings("unchecked")
		private <R> Form<R> _getForm(
			FormBuilderFunction<R> formBuilderFunction) {

			if (formBuilderFunction != null) {
				return formBuilderFunction.apply(_formBuilderSupplier.get());
			}

			return null;
		}

		private S _getId(Object object) {
			Resource.Id id = (Resource.Id)object;

			return unsafeCast(id.asObject());
		}

		private <R> R _getModel(Form<R> form, Supplier<Body> body) {
			if (form != null) {
				return form.get(body.get());
			}

			return null;
		}

		private <I extends Identifier<?>> String _getResourceName(
			Class<I> identifierClass) {

			return _nameFunction.apply(
				identifierClass.getName()
			).orElse(
				null
			);
		}

		private Void _run(CheckedRunnable checkedRunnable) throws Throwable {
			checkedRunnable.run();

			return null;
		}

		private final List<ActionSemantics> _actionSemantics =
			new ArrayList<>();
		private final Supplier<Form.Builder> _formBuilderSupplier;
		private final Item _item;
		private final Function<String, Optional<String>> _nameFunction;

	}

	private final List<ActionSemantics> _actionSemantics;

}
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
import com.liferay.apio.architect.alias.routes.NestedBatchCreateItemFunction;
import com.liferay.apio.architect.alias.routes.NestedCreateItemFunction;
import com.liferay.apio.architect.alias.routes.NestedGetPageFunction;
import com.liferay.apio.architect.alias.routes.permission.HasNestedAddingPermissionFunction;
import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.batch.BatchResult;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.function.throwable.ThrowableBiFunction;
import com.liferay.apio.architect.function.throwable.ThrowableFunction;
import com.liferay.apio.architect.function.throwable.ThrowableHexaFunction;
import com.liferay.apio.architect.function.throwable.ThrowablePentaFunction;
import com.liferay.apio.architect.function.throwable.ThrowableTetraFunction;
import com.liferay.apio.architect.function.throwable.ThrowableTriFunction;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;
import com.liferay.apio.architect.internal.pagination.PageImpl;
import com.liferay.apio.architect.internal.single.model.SingleModelImpl;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.single.model.SingleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alejandro Hernández
 */
public class NestedCollectionRoutesImpl<T, S, U>
	implements NestedCollectionRoutes<T, S, U> {

	public NestedCollectionRoutesImpl(BuilderImpl<T, S, U> builderImpl) {
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
	public Optional<Form> getFormOptional() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<NestedBatchCreateItemFunction<S, U>>
		getNestedBatchCreateItemFunctionOptional() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<NestedCreateItemFunction<T, U>>
		getNestedCreateItemFunctionOptional() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<NestedGetPageFunction<T, U>>
		getNestedGetPageFunctionOptional() {

		throw new UnsupportedOperationException();
	}

	public static class BuilderImpl<T, S, U> implements Builder<T, S, U> {

		public BuilderImpl(
			Nested nested, Supplier<Form.Builder> formBuilderSupplier,
			Function<T, S> modelToIdentifierFunction) {

			_nested = nested;
			_formBuilderSupplier = formBuilderSupplier;
			_modelToIdentifierFunction = modelToIdentifierFunction;
		}

		@Override
		public <R> Builder<T, S, U> addCreator(
			ThrowableBiFunction<U, R, T> creatorThrowableBiFunction,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowableBiFunction<U, List<R>, List<S>>
				batchCreatorThrowableBiFunction = (u, formList) ->
					_transformList(
						formList, r -> creatorThrowableBiFunction.apply(u, r));

			return addCreator(
				creatorThrowableBiFunction, batchCreatorThrowableBiFunction,
				hasNestedAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <R> Builder<T, S, U> addCreator(
			ThrowableBiFunction<U, R, T> creatorThrowableBiFunction,
			ThrowableBiFunction<U, List<R>, List<S>>
				batchCreatorThrowableBiFunction,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_nested
				).name(
					"batch-create"
				).method(
					"POST"
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowableBiFunction.andThen(
						t -> new BatchResult<>(t, _nested.name())
					).apply(
						_getId(params.get(0)), form.getList((Body)params.get(1))
					)
				).receivesParams(
					ParentId.class, Body.class
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"create"
			).method(
				"POST"
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowableBiFunction.andThen(
					t -> new SingleModelImpl<>(t, _nested.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1))
				)
			).receivesParams(
				ParentId.class, Body.class
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D, R> Builder<T, S, U> addCreator(
			ThrowableHexaFunction<U, R, A, B, C, D, T>
				creatorThrowableHexaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowableHexaFunction<U, List<R>, A, B, C, D, List<S>>
				batchCreatorThrowableHexaFunction = (u, formList, a, b, c, d) ->
					_transformList(
						formList,
						r -> creatorThrowableHexaFunction.apply(
							u, r, a, b, c, d));

			return addCreator(
				creatorThrowableHexaFunction, batchCreatorThrowableHexaFunction,
				aClass, bClass, cClass, dClass,
				hasNestedAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <A, B, C, D, R> Builder<T, S, U> addCreator(
			ThrowableHexaFunction<U, R, A, B, C, D, T>
				creatorThrowableHexaFunction,
			ThrowableHexaFunction<U, List<R>, A, B, C, D, List<S>>
				batchCreatorThrowableHexaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_nested
				).name(
					"batch-create"
				).method(
					"POST"
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowableHexaFunction.andThen(
						t -> new BatchResult<>(t, _nested.name())
					).apply(
						_getId(params.get(0)),
						form.getList((Body)params.get(1)),
						unsafeCast(params.get(2)), unsafeCast(params.get(3)),
						unsafeCast(params.get(4)), unsafeCast(params.get(5))
					)
				).receivesParams(
					ParentId.class, Body.class, aClass, bClass, cClass, dClass
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"create"
			).method(
				"POST"
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowableHexaFunction.andThen(
					t -> new SingleModelImpl<>(t, _nested.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4)), unsafeCast(params.get(5))
				)
			).receivesParams(
				ParentId.class, Body.class, aClass, bClass, cClass, dClass
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, C, R> Builder<T, S, U> addCreator(
			ThrowablePentaFunction<U, R, A, B, C, T>
				creatorThrowablePentaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowablePentaFunction<U, List<R>, A, B, C, List<S>>
				batchCreatorThrowablePentaFunction = (u, formList, a, b, c) ->
					_transformList(
						formList,
						r -> creatorThrowablePentaFunction.apply(
							u, r, a, b, c));

			return addCreator(
				creatorThrowablePentaFunction,
				batchCreatorThrowablePentaFunction, aClass, bClass, cClass,
				hasNestedAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <A, B, C, R> Builder<T, S, U> addCreator(
			ThrowablePentaFunction<U, R, A, B, C, T>
				creatorThrowablePentaFunction,
			ThrowablePentaFunction<U, List<R>, A, B, C, List<S>>
				batchCreatorThrowablePentaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_nested
				).name(
					"batch-create"
				).method(
					"POST"
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowablePentaFunction.andThen(
						t -> new BatchResult<>(t, _nested.name())
					).apply(
						_getId(params.get(0)),
						form.getList((Body)params.get(1)),
						unsafeCast(params.get(2)), unsafeCast(params.get(3)),
						unsafeCast(params.get(4))
					)
				).receivesParams(
					ParentId.class, Body.class, aClass, bClass, cClass
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"create"
			).method(
				"POST"
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowablePentaFunction.andThen(
					t -> new SingleModelImpl<>(t, _nested.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4))
				)
			).receivesParams(
				ParentId.class, Body.class, aClass, bClass, cClass
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, R> Builder<T, S, U> addCreator(
			ThrowableTetraFunction<U, R, A, B, T> creatorThrowableTetraFunction,
			Class<A> aClass, Class<B> bClass,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowableTetraFunction<U, List<R>, A, B, List<S>>
				batchCreatorThrowableTetraFunction = (u, formList, a, b) ->
					_transformList(
						formList,
						r -> creatorThrowableTetraFunction.apply(u, r, a, b));

			return addCreator(
				creatorThrowableTetraFunction,
				batchCreatorThrowableTetraFunction, aClass, bClass,
				hasNestedAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <A, B, R> Builder<T, S, U> addCreator(
			ThrowableTetraFunction<U, R, A, B, T> creatorThrowableTetraFunction,
			ThrowableTetraFunction<U, List<R>, A, B, List<S>>
				batchCreatorThrowableTetraFunction,
			Class<A> aClass, Class<B> bClass,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_nested
				).name(
					"batch-create"
				).method(
					"POST"
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowableTetraFunction.andThen(
						t -> new BatchResult<>(t, _nested.name())
					).apply(
						_getId(params.get(0)),
						form.getList((Body)params.get(1)),
						unsafeCast(params.get(2)), unsafeCast(params.get(3))
					)
				).receivesParams(
					ParentId.class, Body.class, aClass, bClass
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"create"
			).method(
				"POST"
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowableTetraFunction.andThen(
					t -> new SingleModelImpl<>(t, _nested.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3))
				)
			).receivesParams(
				ParentId.class, Body.class, aClass, bClass
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, R> Builder<T, S, U> addCreator(
			ThrowableTriFunction<U, R, A, T> creatorThrowableTriFunction,
			Class<A> aClass,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowableTriFunction<U, List<R>, A, List<S>>
				batchCreatorThrowableTriFunction = (u, formList, a) ->
					_transformList(
						formList,
						r -> creatorThrowableTriFunction.apply(u, r, a));

			return addCreator(
				creatorThrowableTriFunction, batchCreatorThrowableTriFunction,
				aClass, hasNestedAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <A, R> Builder<T, S, U> addCreator(
			ThrowableTriFunction<U, R, A, T> creatorThrowableTriFunction,
			ThrowableTriFunction<U, List<R>, A, List<S>>
				batchCreatorThrowableTriFunction,
			Class<A> aClass,
			HasNestedAddingPermissionFunction<U>
				hasNestedAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_nested
				).name(
					"batch-create"
				).method(
					"POST"
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowableTriFunction.andThen(
						t -> new BatchResult<>(t, _nested.name())
					).apply(
						_getId(params.get(0)),
						form.getList((Body)params.get(1)),
						unsafeCast(params.get(2))
					)
				).receivesParams(
					ParentId.class, Body.class, aClass
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"create"
			).method(
				"POST"
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowableTriFunction.andThen(
					t -> new SingleModelImpl<>(t, _nested.name())
				).apply(
					_getId(params.get(0)), form.get((Body)params.get(1)),
					unsafeCast(params.get(2))
				)
			).receivesParams(
				ParentId.class, Body.class, aClass
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public Builder<T, S, U> addGetter(
			ThrowableBiFunction<Pagination, U, PageItems<T>>
				getterThrowableBiFunction) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"retrieve"
			).method(
				"GET"
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowableBiFunction.andThen(
					pageItems -> new PageImpl<>(
						_nested.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), _getId(params.get(1))
				)
			).receivesParams(
				Pagination.class, ParentId.class
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D> Builder<T, S, U> addGetter(
			ThrowableHexaFunction<Pagination, U, A, B, C, D, PageItems<T>>
				getterThrowableHexaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			Class<D> dClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"retrieve"
			).method(
				"GET"
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowableHexaFunction.andThen(
					pageItems -> new PageImpl<>(
						_nested.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), _getId(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4)), unsafeCast(params.get(5))
				)
			).receivesParams(
				Pagination.class, ParentId.class, aClass, bClass, cClass, dClass
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C> Builder<T, S, U> addGetter(
			ThrowablePentaFunction<Pagination, U, A, B, C, PageItems<T>>
				getterThrowablePentaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"retrieve"
			).method(
				"GET"
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowablePentaFunction.andThen(
					pageItems -> new PageImpl<>(
						_nested.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), _getId(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4))
				)
			).receivesParams(
				Pagination.class, ParentId.class, aClass, bClass, cClass
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B> Builder<T, S, U> addGetter(
			ThrowableTetraFunction<Pagination, U, A, B, PageItems<T>>
				getterThrowableTetraFunction,
			Class<A> aClass, Class<B> bClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"retrieve"
			).method(
				"GET"
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowableTetraFunction.andThen(
					pageItems -> new PageImpl<>(
						_nested.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), _getId(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3))
				)
			).receivesParams(
				Pagination.class, ParentId.class, aClass, bClass
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A> Builder<T, S, U> addGetter(
			ThrowableTriFunction<Pagination, U, A, PageItems<T>>
				getterThrowableTriFunction,
			Class<A> aClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_nested
			).name(
				"retrieve"
			).method(
				"GET"
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowableTriFunction.andThen(
					pageItems -> new PageImpl<>(
						_nested.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), _getId(params.get(1)),
					unsafeCast(params.get(2))
				)
			).receivesParams(
				Pagination.class, ParentId.class, aClass
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public NestedCollectionRoutes<T, S, U> build() {
			return new NestedCollectionRoutesImpl<>(this);
		}

		@SuppressWarnings("unchecked")
		private <R> Form<R> _getForm(
			FormBuilderFunction<R> formBuilderFunction) {

			return formBuilderFunction.apply(_formBuilderSupplier.get());
		}

		private U _getId(Object object) {
			Resource.Id id = (Resource.Id)object;

			return unsafeCast(id.asObject());
		}

		private <V> List<S> _transformList(
				List<V> list,
				ThrowableFunction<V, T> transformThrowableFunction)
			throws Exception {

			List<S> newList = new ArrayList<>();

			for (V v : list) {
				S s = transformThrowableFunction.andThen(
					_modelToIdentifierFunction::apply
				).apply(
					v
				);

				newList.add(s);
			}

			return newList;
		}

		private final List<ActionSemantics> _actionSemantics =
			new ArrayList<>();
		private final Supplier<Form.Builder> _formBuilderSupplier;
		private final Function<T, S> _modelToIdentifierFunction;
		private final Nested _nested;

	}

	private final List<ActionSemantics> _actionSemantics;

}
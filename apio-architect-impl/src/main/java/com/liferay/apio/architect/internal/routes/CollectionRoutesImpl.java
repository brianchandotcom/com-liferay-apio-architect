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

import static java.util.Collections.unmodifiableList;

import com.liferay.apio.architect.alias.IdentifierFunction;
import com.liferay.apio.architect.alias.form.FormBuilderFunction;
import com.liferay.apio.architect.alias.routes.BatchCreateItemFunction;
import com.liferay.apio.architect.alias.routes.CreateItemFunction;
import com.liferay.apio.architect.alias.routes.CustomPageFunction;
import com.liferay.apio.architect.alias.routes.GetPageFunction;
import com.liferay.apio.architect.alias.routes.permission.HasAddingPermissionFunction;
import com.liferay.apio.architect.batch.BatchResult;
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
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.internal.form.FormImpl;
import com.liferay.apio.architect.internal.pagination.PageImpl;
import com.liferay.apio.architect.internal.single.model.SingleModelImpl;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.single.model.SingleModel;
import com.liferay.apio.architect.uri.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alejandro Hernández
 */
public class CollectionRoutesImpl<T, S> implements CollectionRoutes<T, S> {

	public CollectionRoutesImpl(BuilderImpl<T, S> builderImpl) {
		_actionSemantics = unmodifiableList(builderImpl._actionSemantics);
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
	public Optional<BatchCreateItemFunction<S>>
		getBatchCreateItemFunctionOptional() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<CreateItemFunction<T>> getCreateItemFunctionOptional() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Map<String, CustomPageFunction<?>>>
		getCustomPageFunctionsOptional() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, CustomRoute> getCustomRoutes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Form> getFormOptional() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<GetPageFunction<T>> getGetPageFunctionOptional() {
		throw new UnsupportedOperationException();
	}

	public static class BuilderImpl<T, S> implements Builder<T, S> {

		public BuilderImpl(
			Paged paged, Function<Path, ?> pathToIdentifierFunction,
			Function<T, S> modelToIdentifierFunction,
			Function<String, Optional<String>> nameFunction) {

			_paged = paged;
			_pathToIdentifierFunction = pathToIdentifierFunction::apply;
			_modelToIdentifierFunction = modelToIdentifierFunction;
			_nameFunction = nameFunction;
		}

		@Override
		public <A, R> Builder<T, S> addCreator(
			ThrowableBiFunction<R, A, T> creatorThrowableBiFunction,
			Class<A> aClass,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowableBiFunction<List<R>, A, List<S>>
				batchCreatorThrowableBiFunction = (formList, a) ->
					_transformList(
						formList, r -> creatorThrowableBiFunction.apply(r, a));

			return addCreator(
				creatorThrowableBiFunction, batchCreatorThrowableBiFunction,
				aClass, hasAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <A, R> Builder<T, S> addCreator(
			ThrowableBiFunction<R, A, T> creatorThrowableBiFunction,
			ThrowableBiFunction<List<R>, A, List<S>>
				batchCreatorThrowableBiFunction,
			Class<A> aClass,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = formBuilderFunction.apply(
				new FormImpl.BuilderImpl<>(
					_pathToIdentifierFunction, _nameFunction));

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_paged
				).name(
					"batch-create"
				).method(
					"POST"
				).receivesParams(
					Body.class, aClass
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowableBiFunction.andThen(
						t -> new BatchResult<>(t, _paged.name())
					).apply(
						form.getList((Body)params.get(0)),
						unsafeCast(params.get(1))
					)
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"create"
			).method(
				"POST"
			).receivesParams(
				Body.class, aClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowableBiFunction.andThen(
					t -> new SingleModelImpl<>(t, _paged.name())
				).apply(
					form.get((Body)params.get(0)), unsafeCast(params.get(1))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <R> Builder<T, S> addCreator(
			ThrowableFunction<R, T> creatorThrowableFunction,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowableFunction<List<R>, List<S>> batchCreatorThrowableFunction =
				formList -> _transformList(formList, creatorThrowableFunction);

			return addCreator(
				creatorThrowableFunction, batchCreatorThrowableFunction,
				hasAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <R> Builder<T, S> addCreator(
			ThrowableFunction<R, T> creatorThrowableFunction,
			ThrowableFunction<List<R>, List<S>> batchCreatorThrowableFunction,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = formBuilderFunction.apply(
				new FormImpl.BuilderImpl<>(
					_pathToIdentifierFunction, _nameFunction));

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_paged
				).name(
					"batch-create"
				).method(
					"POST"
				).receivesParams(
					Body.class
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowableFunction.andThen(
						t -> new BatchResult<>(t, _paged.name())
					).apply(
						form.getList((Body)params.get(0))
					)
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"create"
			).method(
				"POST"
			).receivesParams(
				Body.class
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowableFunction.andThen(
					t -> new SingleModelImpl<>(t, _paged.name())
				).apply(
					form.get((Body)params.get(0))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D, R> Builder<T, S> addCreator(
			ThrowablePentaFunction<R, A, B, C, D, T>
				creatorThrowablePentaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowablePentaFunction<List<R>, A, B, C, D, List<S>>
				batchCreatorThrowablePentaFunction = (formList, a, b, c, d) ->
					_transformList(
						formList,
						r -> creatorThrowablePentaFunction.apply(
							r, a, b, c, d));

			return addCreator(
				creatorThrowablePentaFunction,
				batchCreatorThrowablePentaFunction, aClass, bClass, cClass,
				dClass, hasAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <A, B, C, D, R> Builder<T, S> addCreator(
			ThrowablePentaFunction<R, A, B, C, D, T>
				creatorThrowablePentaFunction,
			ThrowablePentaFunction<List<R>, A, B, C, D, List<S>>
				batchCreatorThrowablePentaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = formBuilderFunction.apply(
				new FormImpl.BuilderImpl<>(
					_pathToIdentifierFunction, _nameFunction));

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_paged
				).name(
					"batch-create"
				).method(
					"POST"
				).receivesParams(
					Body.class, aClass, bClass, cClass, dClass
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowablePentaFunction.andThen(
						t -> new BatchResult<>(t, _paged.name())
					).apply(
						form.getList((Body)params.get(0)),
						unsafeCast(params.get(1)), unsafeCast(params.get(2)),
						unsafeCast(params.get(3)), unsafeCast(params.get(4))
					)
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"create"
			).method(
				"POST"
			).receivesParams(
				Body.class, aClass, bClass, cClass, dClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowablePentaFunction.andThen(
					t -> new SingleModelImpl<>(t, _paged.name())
				).apply(
					form.get((Body)params.get(0)), unsafeCast(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, C, R> Builder<T, S> addCreator(
			ThrowableTetraFunction<R, A, B, C, T> creatorThrowableTetraFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowableTetraFunction<List<R>, A, B, C, List<S>>
				batchCreatorThrowableTetraFunction = (formList, a, b, c) ->
					_transformList(
						formList,
						r -> creatorThrowableTetraFunction.apply(r, a, b, c));

			return addCreator(
				creatorThrowableTetraFunction,
				batchCreatorThrowableTetraFunction, aClass, bClass, cClass,
				hasAddingPermissionFunction, formBuilderFunction);
		}

		@Override
		public <A, B, C, R> Builder<T, S> addCreator(
			ThrowableTetraFunction<R, A, B, C, T> creatorThrowableTetraFunction,
			ThrowableTetraFunction<List<R>, A, B, C, List<S>>
				batchCreatorThrowableTetraFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = formBuilderFunction.apply(
				new FormImpl.BuilderImpl<>(
					_pathToIdentifierFunction, _nameFunction));

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_paged
				).name(
					"batch-create"
				).method(
					"POST"
				).receivesParams(
					Body.class, aClass, bClass, cClass
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowableTetraFunction.andThen(
						t -> new BatchResult<>(t, _paged.name())
					).apply(
						form.getList((Body)params.get(0)),
						unsafeCast(params.get(1)), unsafeCast(params.get(2)),
						unsafeCast(params.get(3))
					)
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"create"
			).method(
				"POST"
			).receivesParams(
				Body.class, aClass, bClass, cClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowableTetraFunction.andThen(
					t -> new SingleModelImpl<>(t, _paged.name())
				).apply(
					form.get((Body)params.get(0)), unsafeCast(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, R> Builder<T, S> addCreator(
			ThrowableTriFunction<R, A, B, T> creatorThrowableTriFunction,
			Class<A> aClass, Class<B> bClass,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			ThrowableTriFunction<List<R>, A, B, List<S>>
				batchCreatorThrowableTriFunction = (formList, a, b) ->
					_transformList(
						formList,
						r -> creatorThrowableTriFunction.apply(r, a, b));

			return addCreator(
				creatorThrowableTriFunction, batchCreatorThrowableTriFunction,
				aClass, bClass, hasAddingPermissionFunction,
				formBuilderFunction);
		}

		@Override
		public <A, B, R> Builder<T, S> addCreator(
			ThrowableTriFunction<R, A, B, T> creatorThrowableTriFunction,
			ThrowableTriFunction<List<R>, A, B, List<S>>
				batchCreatorThrowableTriFunction,
			Class<A> aClass, Class<B> bClass,
			HasAddingPermissionFunction hasAddingPermissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = formBuilderFunction.apply(
				new FormImpl.BuilderImpl<>(
					_pathToIdentifierFunction, _nameFunction));

			ActionSemantics batchCreateActionSemantics =
				ActionSemantics.ofResource(
					_paged
				).name(
					"batch-create"
				).method(
					"POST"
				).receivesParams(
					Body.class, aClass, bClass
				).returns(
					BatchResult.class
				).executeFunction(
					params -> batchCreatorThrowableTriFunction.andThen(
						t -> new BatchResult<>(t, _paged.name())
					).apply(
						form.getList((Body)params.get(0)),
						unsafeCast(params.get(1)), unsafeCast(params.get(2))
					)
				).build();

			_actionSemantics.add(batchCreateActionSemantics);

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"create"
			).method(
				"POST"
			).receivesParams(
				Body.class, aClass, bClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> creatorThrowableTriFunction.andThen(
					t -> new SingleModelImpl<>(t, _paged.name())
				).apply(
					form.get((Body)params.get(0)), unsafeCast(params.get(1)),
					unsafeCast(params.get(2))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <R, U, I extends Identifier> Builder<T, S> addCustomRoute(
			CustomRoute customRoute,
			ThrowableBiFunction<Pagination, R, U> throwableBiFunction,
			Class<I> supplier,
			Function<Credentials, Boolean> permissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Pagination.class, bodyClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> throwableBiFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					(Pagination)params.get(0),
					_getModel(form, () -> (Body)params.get(1))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D, R, U, I extends Identifier>
			Builder<T, S> addCustomRoute(
				CustomRoute customRoute,
				ThrowableHexaFunction<Pagination, R, A, B, C, D, U>
					throwableHexaFunction,
				Class<A> aClass, Class<B> bClass, Class<C> cClass,
				Class<D> dClass, Class<I> supplier,
				Function<Credentials, Boolean> permissionFunction,
				FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Pagination.class, bodyClass, aClass, bClass, cClass, dClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> throwableHexaFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					(Pagination)params.get(0),
					_getModel(form, () -> (Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4)), unsafeCast(params.get(5))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, C, R, U, I extends Identifier>
			Builder<T, S> addCustomRoute(
				CustomRoute customRoute,
				ThrowablePentaFunction<Pagination, R, A, B, C, U>
					throwablePentaFunction,
				Class<A> aClass, Class<B> bClass, Class<C> cClass,
				Class<I> supplier,
				Function<Credentials, Boolean> permissionFunction,
				FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Pagination.class, bodyClass, aClass, bClass, cClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> throwablePentaFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					(Pagination)params.get(0),
					_getModel(form, () -> (Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, B, R, U, I extends Identifier> Builder<T, S> addCustomRoute(
			CustomRoute customRoute,
			ThrowableTetraFunction<Pagination, R, A, B, U>
				throwableTetraFunction,
			Class<A> aClass, Class<B> bClass, Class<I> supplier,
			Function<Credentials, Boolean> permissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Pagination.class, bodyClass, aClass, bClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> throwableTetraFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					(Pagination)params.get(0),
					_getModel(form, () -> (Body)params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A, R, U, I extends Identifier> Builder<T, S> addCustomRoute(
			CustomRoute customRoute,
			ThrowableTriFunction<Pagination, R, A, U> throwableTriFunction,
			Class<A> aClass, Class<I> supplier,
			Function<Credentials, Boolean> permissionFunction,
			FormBuilderFunction<R> formBuilderFunction) {

			Form<R> form = _getForm(formBuilderFunction);

			Class<?> bodyClass = form == null ? Void.class : Body.class;

			ActionSemantics createActionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				customRoute.getName()
			).method(
				customRoute.getMethod()
			).receivesParams(
				Pagination.class, bodyClass, aClass
			).returns(
				SingleModel.class
			).executeFunction(
				params -> throwableTriFunction.andThen(
					t -> new SingleModelImpl<>(t, _getResourceName(supplier))
				).apply(
					(Pagination)params.get(0),
					_getModel(form, () -> (Body)params.get(1)),
					unsafeCast(params.get(2))
				)
			).build();

			_actionSemantics.add(createActionSemantics);

			return this;
		}

		@Override
		public <A> Builder<T, S> addGetter(
			ThrowableBiFunction<Pagination, A, PageItems<T>>
				getterThrowableBiFunction,
			Class<A> aClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Pagination.class, aClass
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowableBiFunction.andThen(
					pageItems -> new PageImpl<>(
						_paged.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), unsafeCast(params.get(1))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public Builder<T, S> addGetter(
			ThrowableFunction<Pagination, PageItems<T>>
				getterThrowableFunction) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Pagination.class
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowableFunction.andThen(
					pageItems -> new PageImpl<>(
						_paged.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0)
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C, D> Builder<T, S> addGetter(
			ThrowablePentaFunction<Pagination, A, B, C, D, PageItems<T>>
				getterThrowablePentaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass,
			Class<D> dClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Pagination.class, aClass, bClass, cClass, dClass
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowablePentaFunction.andThen(
					pageItems -> new PageImpl<>(
						_paged.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), unsafeCast(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3)),
					unsafeCast(params.get(4))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B, C> Builder<T, S> addGetter(
			ThrowableTetraFunction<Pagination, A, B, C, PageItems<T>>
				getterThrowableTetraFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Pagination.class, aClass, bClass, cClass
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowableTetraFunction.andThen(
					pageItems -> new PageImpl<>(
						_paged.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), unsafeCast(params.get(1)),
					unsafeCast(params.get(2)), unsafeCast(params.get(3))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public <A, B> Builder<T, S> addGetter(
			ThrowableTriFunction<Pagination, A, B, PageItems<T>>
				getterThrowableTriFunction,
			Class<A> aClass, Class<B> bClass) {

			ActionSemantics actionSemantics = ActionSemantics.ofResource(
				_paged
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesParams(
				Pagination.class, aClass, bClass
			).returns(
				Page.class
			).executeFunction(
				params -> getterThrowableTriFunction.andThen(
					pageItems -> new PageImpl<>(
						_paged.name(), pageItems, (Pagination)params.get(0))
				).apply(
					(Pagination)params.get(0), unsafeCast(params.get(1)),
					unsafeCast(params.get(2))
				)
			).build();

			_actionSemantics.add(actionSemantics);

			return this;
		}

		@Override
		public CollectionRoutes<T, S> build() {
			return new CollectionRoutesImpl<>(this);
		}

		private <R> Form<R> _getForm(
			FormBuilderFunction<R> formBuilderFunction) {

			if (formBuilderFunction != null) {
				return formBuilderFunction.apply(
					new FormImpl.BuilderImpl<>(
						_pathToIdentifierFunction, _nameFunction));
			}

			return null;
		}

		private <R> R _getModel(Form<R> form, Supplier<Body> body) {
			if (form != null) {
				return form.get(body.get());
			}

			return null;
		}

		private <I extends Identifier> String _getResourceName(Class<I> clazz) {
			return _nameFunction.apply(
				clazz.getName()
			).orElse(
				null
			);
		}

		private <U> List<S> _transformList(
				List<U> list,
				ThrowableFunction<U, T> transformThrowableFunction)
			throws Exception {

			List<S> newList = new ArrayList<>();

			for (U u : list) {
				S s = transformThrowableFunction.andThen(
					_modelToIdentifierFunction::apply
				).apply(
					u
				);

				newList.add(s);
			}

			return newList;
		}

		private final List<ActionSemantics> _actionSemantics =
			new ArrayList<>();
		private final Function<T, S> _modelToIdentifierFunction;
		private final Function<String, Optional<String>> _nameFunction;
		private final Paged _paged;
		private final IdentifierFunction<?> _pathToIdentifierFunction;

	}

	private final List<ActionSemantics> _actionSemantics;

}
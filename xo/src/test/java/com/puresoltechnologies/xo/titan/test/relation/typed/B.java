package com.puresoltechnologies.xo.titan.test.relation.typed;

import java.util.List;

import com.puresoltechnologies.ductiledb.xo.api.annotation.VertexDefinition;

@VertexDefinition("B")
public interface B {

	TypedOneToOneRelation getOneToOne();

	TypedOneToManyRelation getManyToOne();

	List<TypedManyToManyRelation> getManyToMany();

}

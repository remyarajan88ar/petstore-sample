CREATE ALIAS GetCategoryByName AS
$$
  SELECT c FROM Category c WHERE c.name =?;
$$;
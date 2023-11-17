INSERT INTO inorganic (formula, stock_name, systematic_name, traditional_name, common_name, molecular_mass, density, melting_point, boiling_point)

VALUES (
# Formula (use digits instead of subscripts, for peroxydes use "(O2)"):
,
# Names (it's "...()", not "... ()"):
	, # Stock name
	, # Systematic name
	, # Traditional name
	, # Common name
# Properties (use "." instead of "," don't write units, just numbers):
	, # Molecular mass (in g / mol, greater than 1, with 2 decimal places, can be .00)
	, # Density (in g / cm3, 6 decimal places at most or 3 significant, can NOT be ._0 or .0)
	, # Melting point (in K, > 1, 2 decimal places at most, can NOT be ._0 , .0 or .15)
	  # Boiling point (in K, > 1, 2 decimal places at most, can NOT be ._0, .0 .15)
);
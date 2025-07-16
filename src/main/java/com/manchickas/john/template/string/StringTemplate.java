package com.manchickas.john.template.string;

import com.manchickas.john.template.Template;

public interface StringTemplate extends Template<String> {

    Template<String> caseInsensitive();
}

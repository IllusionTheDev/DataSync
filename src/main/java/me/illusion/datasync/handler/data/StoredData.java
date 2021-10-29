package me.illusion.datasync.handler.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class StoredData implements Serializable {

    private final Map<String, Object> data;
}

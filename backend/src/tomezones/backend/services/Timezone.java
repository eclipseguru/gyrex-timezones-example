package tomezones.backend.services;

public class Timezone {

	private String name, city;
	private int offset;

	public String getCity() {
		return city;
	}

	public String getName() {
		return name;
	}

	public int getOffset() {
		return offset;
	}

	public void setCity(final String city) {
		if (city == null) {
			throw new IllegalArgumentException("null city");
		}
		this.city = city;
	}

	public void setName(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("null name");
		}
		this.name = name;
	}

	public void setOffset(final int offset) {
		if ((offset < -12) || (offset > 12)) {
			throw new IllegalArgumentException("invalid offset");
		}
		this.offset = offset;
	}

}
